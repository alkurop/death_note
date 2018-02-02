package com.omar.deathnote.notes.picture

import com.alkurop.database.ContentDao
import com.omar.deathnote.Constants
import com.omar.deathnote.notes.BaseContentPresenter
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject

class PicturePresenter(contentDao: ContentDao) : BaseContentPresenter(contentDao) {
    val navSubject = PublishSubject.create<OpenImageViewrNavigation>()
    fun openImageViewer() {
        val parentNoteId = content.parentNoteId
        contentDao.getRelatedToNote(parentNoteId)
            .take(1)
            .subscribeOn(io())
            .subscribe {
                val pictures = it.filter { it.type == Constants.Frags.PicFragment.ordinal }
                    .map { it.id }

                val position = pictures.indexOf(content.id)

                val navigationModel = OpenImageViewrNavigation(position, pictures.map { it.toString() })
                navSubject.onNext(navigationModel)
            }
    }

}

data class OpenImageViewrNavigation(
        val position: Int,
        val imageContentListSorted: List<String>
)
