package com.omar.deathnote.notes.content.audio

import android.view.View
import com.alkurop.database.Content
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import javax.inject.Inject

class AudioViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var presenter: AudioPresenter

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
    }
}
