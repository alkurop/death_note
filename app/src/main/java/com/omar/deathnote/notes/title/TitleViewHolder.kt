package com.omar.deathnote.notes.title

import android.view.View
import com.alkurop.database.Content1
import com.jakewharton.rxbinding2.widget.RxTextView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.ContentViewHolder
import kotlinx.android.synthetic.main.note_elem_title.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TitleViewHolder(itemView: View)
    : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: TitlePresenter

    override fun bind(content: Content1) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.tvTitle.setText(content.content)
        RxTextView.afterTextChangeEvents(itemView.tvTitle)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe {
                    content.content = it.editable()?.toString()
                    presenter.save()
                }
    }
}
