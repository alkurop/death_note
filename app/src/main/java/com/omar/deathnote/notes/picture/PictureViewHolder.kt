package com.omar.deathnote.notes.picture

import android.view.View
import com.alkurop.database.Content1
import com.jakewharton.rxbinding2.view.RxView
import com.omar.deathnote.ComponentContainer
import com.omar.deathnote.notes.ContentViewComponent
import com.omar.deathnote.notes.ContentViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.note_elem_pic.view.contentView
import kotlinx.android.synthetic.main.note_elem_pic.view.del
import javax.inject.Inject

class PictureViewHolder(itemView: View?) : ContentViewHolder(itemView) {

    @Inject
    lateinit var presenter: PicturePresenter

    override fun bind(content: Content1) {
        ComponentContainer.instance[ContentViewComponent::class.java].inject(this)
        presenter.content = content
        RxView.clicks(itemView.del).subscribe { presenter.delete() }
        Picasso.with(itemView.context).load(content.content).into(itemView.contentView)

    }
}
