package com.omar.deathnote.notes.content.link

import com.alkurop.database.ContentDao
import com.leocardz.link.preview.library.SourceContent
import com.omar.deathnote.notes.content.BaseContentPresenter
import com.omar.deathnote.utility.LinkLoader
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class LinkPresenter @Inject constructor(
        contentDao: ContentDao,
        val linkLoader: LinkLoader
) : BaseContentPresenter(contentDao) {
    val sourceContentPublisher = PublishSubject.create<SourceContent>()

    fun getLinkContent(url: String): Disposable = linkLoader
        .getLink(url)
        .subscribeOn(io())
        .subscribe {
            sourceContentPublisher.onNext(it)
        }

}
