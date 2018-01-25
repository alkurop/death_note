package com.omar.deathnote.notes.audio

import android.view.View
import com.alkurop.database.Content1
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.ContentViewHolder
import javax.inject.Inject

class AudioViewHolder(itemView: View?)
    : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: AudioPresenter

    override fun bind(content: Content1) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
    }
}
