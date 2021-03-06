package com.omar.deathnote.utility

import android.util.Patterns
import com.alkurop.database.Link
import com.alkurop.database.LinkDao
import com.google.gson.Gson
import com.leocardz.link.preview.library.LinkPreviewCallback
import com.leocardz.link.preview.library.SourceContent
import com.leocardz.link.preview.library.TextCrawler
import com.omar.deathnote.notes.ContentViewScope
import io.reactivex.Maybe
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ContentViewScope
class LinkLoader @Inject constructor(
        val linkDao: LinkDao,
        val scheduler: Scheduler,
        val gson: Gson
) {

    private val textCrawler = TextCrawler()

    fun getLink(path: String): Maybe<SourceContent> {
        val convertedPath =
            if (path.startsWith("http://") || path.startsWith("https://"))
                path
            else "https://$path"

        if (Patterns.WEB_URL.matcher(convertedPath).matches().not()) {
            Maybe.empty<SourceContent>()
        }
        val apiObservable = getLinkFromApi(convertedPath)
            .toObservable()
            .flatMap {
                val link = it.toLink(convertedPath)
                linkDao.addOrUpdate(link)
                Observable.just(link)
            }
        return linkDao.getByPath(convertedPath)
            .toObservable()
            .publish { share ->
                val expired = share.filter { it.isExpired() }
                    .flatMap { localLink ->

                        apiObservable
                            .switchIfEmpty {
                                Observable.just(localLink)
                            }
                    }

                val notExpired = share.filter { !it.isExpired() }
                Observable.merge(expired, notExpired)

            }
            .switchIfEmpty(apiObservable)
            .firstElement()
            .subscribeOn(scheduler)
            .map { it.toSourceContent() }
    }

    private fun getLinkFromApi(path: String): Maybe<SourceContent> {
        val apiResultPublisher = PublishSubject.create<Notification<SourceContent>>()

        val callback = object : LinkPreviewCallback {
            override fun onPre() {
                //noop
            }

            override fun onPos(content: SourceContent?, isNull: Boolean) {
                if (isNull.not() && content != null) {
                    apiResultPublisher.onNext(Notification.createOnNext(content))
                } else {
                    apiResultPublisher.onNext(Notification.createOnError(RuntimeException("Could not load content")))
                }
            }
        }
        textCrawler.makePreview(callback, path)
        return apiResultPublisher
            .dematerialize<SourceContent>()
            .onErrorResumeNext(Function { error ->
                Timber.e(error)
                Observable.empty<SourceContent>()
            })
            .firstElement()
            .observeOn(scheduler)
    }

    private fun Link.isExpired(): Boolean {
        val expirationPeriond = TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)
        val isExpired = timeStamp!! < System.currentTimeMillis() - expirationPeriond
        return isExpired
    }

    private fun SourceContent.toLink(requestPath: String): Link {
        return Link().apply {
            timeStamp = System.currentTimeMillis()
            content = gson.toJson(this@toLink)
            path = requestPath
        }
    }

    private fun Link.toSourceContent(): SourceContent {
        return gson.fromJson(this.content, SourceContent::class.java)
    }
}
