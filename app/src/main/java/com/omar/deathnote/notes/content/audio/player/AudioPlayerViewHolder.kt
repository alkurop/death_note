package com.omar.deathnote.notes.content.audio.player

import android.view.View
import com.alkurop.database.Content
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.content.ContentViewHolder
import com.omar.deathnote.notes.content.audio.recorder.AudioRecorderPresenter
import javax.inject.Inject

class AudioPlayerViewHolder(
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
