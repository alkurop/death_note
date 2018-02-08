package com.omar.deathnote.notes.content.note

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
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

class NoteViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var presenter: NotePresenter

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        itemView.etText.setText(content.content)
        RxTextView.afterTextChangeEvents(itemView.etText)
            .debounce(1, TimeUnit.SECONDS)
            .subscribe {
                content.content = it.editable()?.toString()
                presenter.save()
            }
        Linkify.addLinks(itemView.etText, Linkify.ALL)
        itemView.etText.movementMethod = LinkMovementMethod.getInstance();
        RxView.clicks(itemView.del).subscribe { onDeleteCallback.invoke(content.id) }

    }
}
