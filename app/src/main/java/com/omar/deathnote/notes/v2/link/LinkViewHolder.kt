package com.omar.deathnote.notes.v2.link

import android.view.View
import com.alkurop.database.Content1
import com.jakewharton.rxbinding2.widget.RxTextView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.v2.ContentViewComponent
import com.omar.deathnote.notes.v2.ContentViewHolder
import kotlinx.android.synthetic.main.note_elem_link.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LinkViewHolder(itemView: View?)
    : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: LinkPresenter

    override fun bind(content: Content1) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.etText.setText(content.content)
        RxTextView.afterTextChangeEvents(itemView.etText)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe {
                    content.content = it.editable()?.toString()
                    presenter.save()
                }
    }
}
