package com.omar.deathnote.notes.content.title

import android.view.View
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.widget.RxTextView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import kotlinx.android.synthetic.main.note_elem_title.view.tvTitle
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TitleViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var presenter: TitlePresenter

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.tvTitle.setText(content.content)
        RxTextView.afterTextChangeEvents(itemView.tvTitle)
            .debounce(1, TimeUnit.SECONDS)
            .subscribe {
                content.content = it.editable()?.toString()?.trim()
                presenter.save()
            }
    }
}
