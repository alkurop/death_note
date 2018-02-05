package com.omar.deathnote.notes.content.audio.recorder

import android.view.View
import com.alkurop.database.Content
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import javax.inject.Inject

class AudioRecorderViewHolder(
        itemView: View?,
        onDeleteCallback: (Long) -> Unit
) : ContentViewHolder(itemView, onDeleteCallback) {

    @Inject
    lateinit var reecorderPresenter: AudioRecorderPresenter

    override fun bind(content: Content) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        reecorderPresenter.content = content
    }
}
