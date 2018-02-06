package com.omar.deathnote.notes.content

import android.support.v7.util.DiffUtil
import com.alkurop.database.Content

class ContentAdapterDiffUtilCallback(
        val oldList: List<Content>,
        val newList: List<Content>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.content.equals(newItem.content)
                && oldItem.additionalContent == newItem.additionalContent
    }
}
