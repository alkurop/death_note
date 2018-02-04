package com.omar.deathnote.notes.content

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alkurop.database.Content
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.notes.content.audio.AudioViewHolder
import com.omar.deathnote.notes.content.link.LinkViewHolder
import com.omar.deathnote.notes.content.note.NoteViewHolder
import com.omar.deathnote.notes.content.picture.PictureViewHolder
import com.omar.deathnote.notes.content.title.TitleViewHolder

class ContentAdapter(val onDeleteCallback: (Long) -> Unit) : RecyclerView.Adapter<ContentViewHolder>() {
    private var items = listOf<Content>()
    lateinit var layouInflater: LayoutInflater

    fun updateList(newItems: List<Content>) {
        if (newItems == items) return
        val calculateDiff = DiffUtil.calculateDiff(ContentAdapterDiffUtilCallback(items, newItems))
        items = newItems
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layouInflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContentViewHolder {
        return when (viewType) {
            Constants.Frags.TitleFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_title, parent, false)
                TitleViewHolder(view, onDeleteCallback)
            }
            Constants.Frags.AudioFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_audio, parent, false)
                AudioViewHolder(view, onDeleteCallback)
            }
            Constants.Frags.LinkFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_link, parent, false)
                LinkViewHolder(view, onDeleteCallback)
            }
            Constants.Frags.NoteFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_note, parent, false)
                NoteViewHolder(view, onDeleteCallback)
            }
            Constants.Frags.PicFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_pic, parent, false)
                PictureViewHolder(view, onDeleteCallback)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onViewRecycled(holder: ContentViewHolder) {
        holder.unbind()
    }

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: ContentViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        items[position].type
}

abstract class ContentViewHolder(itemView: View?, val onDeleteCallback: (Long) -> Unit) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(content: Content)
    open fun unbind() {}
}
