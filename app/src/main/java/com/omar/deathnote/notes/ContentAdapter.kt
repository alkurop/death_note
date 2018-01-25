package com.omar.deathnote.notes

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alkurop.database.Content1
import com.omar.deathnote.Constants
import com.omar.deathnote.R
import com.omar.deathnote.notes.v2.audio.AudioViewHolder
import com.omar.deathnote.notes.v2.link.LinkViewHolder
import com.omar.deathnote.notes.v2.note.NoteViewHolder
import com.omar.deathnote.notes.v2.picture.PictureViewHolder
import com.omar.deathnote.notes.v2.title.TitleViewHolder

class ContentAdapter : RecyclerView.Adapter<ContentViewHolder>() {
    private var items = listOf<Content1>()
    lateinit var layouInflater: LayoutInflater

    fun updateList(newItems: List<Content1>) {
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
                TitleViewHolder(view)
            }
            Constants.Frags.AudioFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_audio, parent, false)
                AudioViewHolder(view)
            }
            Constants.Frags.LinkFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_link, parent, false)
                LinkViewHolder(view)
            }
            Constants.Frags.NoteFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_note, parent, false)
                NoteViewHolder(view)
            }
            Constants.Frags.PicFragment.ordinal -> {
                val view = layouInflater.inflate(R.layout.note_elem_pic, parent, false)
                PictureViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }

    }

    override fun getItemCount(): Int =
            items.size

    override fun onBindViewHolder(holder: ContentViewHolder?, position: Int) {
        holder?.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
            items[position].type
}

abstract class ContentViewHolder(itemView: View?)
    : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(content: Content1)
}
