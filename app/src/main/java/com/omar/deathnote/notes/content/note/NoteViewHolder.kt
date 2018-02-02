package com.omar.deathnote.notes.note

import android.view.View
import com.alkurop.database.Content
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import kotlinx.android.synthetic.main.note_elem_note.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteViewHolder(itemView: View?)
    : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: NotePresenter

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.etTxt.setText(content.content)
        RxTextView.afterTextChangeEvents(itemView.etTxt)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe {
                    content.content = it.editable()?.toString()
                    presenter.save()
                }
        RxView.clicks(itemView.del).subscribe { presenter.delete() }

    }
}
