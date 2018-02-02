package com.omar.deathnote.notes.content

import com.alkurop.database.Content
import com.alkurop.database.ContentDao
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

abstract class BaseContentPresenter(val contentDao: ContentDao) {
    lateinit var content: Content

    fun save() {
        if (content.parentNoteId != 0L) {
            Completable
                .fromAction {
                    contentDao.addOrUpdate(content)
                }.subscribeOn(Schedulers.io()).subscribe()
        }
    }

    fun delete() {
        Completable
            .fromAction {
                contentDao.delete(content.id)
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}
