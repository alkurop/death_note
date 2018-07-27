package com.omar.deathnote.main

import android.support.v7.util.DiffUtil

class MainAdapterDiffUtilCallback(
        private val oldList: List<NoteViewModel>,
        private val newList: List<NoteViewModel>
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
        return oldList[oldItemPosition].timedate == newList[newItemPosition].timedate
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].style == newList[newItemPosition].style
    }

}
