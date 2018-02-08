package com.omar.deathnote.notes.content.link

import android.content.Intent
import android.net.Uri
import android.view.View
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.leocardz.link.preview.library.SourceContent
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import com.omar.deathnote.utility.plusAssign
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.note_elem_link.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LinkViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var presenter: LinkPresenter

    val dis = CompositeDisposable()
    var currentContent: SourceContent? = null

    override fun bind(content: Content) {

        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.etText.setText(content.content)

        RxTextView
            .afterTextChangeEvents(itemView.etText)
            .debounce(1, TimeUnit.SECONDS)
            .subscribe {
                content.content = it.editable()?.toString()
                presenter.save()
                dis += presenter.getLinkContent(it.editable()!!.toString())
            }

        RxView.clicks(itemView.del).subscribe { onDeleteCallback.invoke(content.id) }

        dis += presenter.sourceContentPublisher
            .observeOn(mainThread())
            .subscribe { itemView.linkContent.setContent(it) }

        RxView.clicks(itemView.linkContent)
            .subscribe {
                currentContent?.let {
                    val uri = Uri.parse(it.cannonicalUrl)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    itemView.context.startActivity(intent)
                }
            }
    }

    override fun unbind() {
        dis.clear()
    }
}
