package com.omar.deathnote.notes.v2

import com.alkurop.database.Content
import com.alkurop.database.ContentDao
import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

sealed class ContentViewState {
    data class UpdateNote(val content: List<Content>,
                          val note: Note) : ContentViewState()
}

sealed class ContentAction {
    object CreateNewNote : ContentAction()
    data class OpenNoteById(val id: Long) : ContentAction()
}

sealed class ContentNavigation {}

class ContentPresenter(private val noteDao: NoteDao,
                       private val contentDao: ContentDao) {
    private val compositeDisposable = CompositeDisposable()

    val viewState = BehaviorSubject.create<ContentViewState>()
    val navigation = PublishSubject.create<ContentNavigation>()

    fun dispose() {
        compositeDisposable.clear()
    }
}
