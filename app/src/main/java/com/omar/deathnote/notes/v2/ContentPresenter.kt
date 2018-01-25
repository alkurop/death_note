package com.omar.deathnote.notes.v2

import com.alkurop.database.Content1
import com.alkurop.database.ContentDao
import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import com.omar.deathnote.Constants
import com.omar.deathnote.utility.plusAssign
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

val DEFAULT_STYLE = 1

data class NoteViewModel(val content: List<Content1>? = null,
                         val style: Int = DEFAULT_STYLE,
                         val noteId: Long = 0)

sealed class ContentAction {
    object          CreateNewNote : ContentAction()
    data class      OpenNoteById(val id: Long) : ContentAction()
    object          Save : ContentAction()
    object          Add : ContentAction()
    data class      DeleteContent(val id: Long) : ContentAction()
    data class      UpdateStyle(val style: Int) : ContentAction()
}

sealed class ContentNavigation {
}

class ContentPresenter(private val noteDao: NoteDao,
                       private val contentDao: ContentDao) {
    private val dis = CompositeDisposable()
    private val viewStatePublisher = BehaviorSubject.create<NoteViewModel>()

    val viewState: Observable<NoteViewModel> = viewStatePublisher
            .scan(NoteViewModel(), { old, new ->
                NoteViewModel(
                        content = new.content ?: old.content,
                        style = if (new.style != 0) new.style else old.style,
                        noteId = if (new.noteId != 0L) new.noteId else old.noteId)
            })
    val navigation = PublishSubject.create<ContentNavigation>()

    fun dispose() {
        dis.clear()
    }

    fun onAction(action: ContentAction) {
        when (action) {
            is ContentAction.CreateNewNote -> createNote()
            is ContentAction.OpenNoteById -> openNote(action.id)
            is ContentAction.Save -> saveNote()
            is ContentAction.DeleteContent -> deleteContent(action.id)
            is ContentAction.UpdateStyle ->
                viewStatePublisher.onNext(NoteViewModel(style = action.style))
        }
    }

    private fun deleteContent(id: Long) {
        contentDao.deleteRelatedToNote(id)
    }

    private fun openNote(id: Long) {
        if (viewStatePublisher.hasValue()) {
            return
        }
        dis += Flowable.combineLatest(contentDao.getRelatedToNote(id),
                noteDao.getById(id),
                BiFunction<List<Content1>, Note, NoteViewModel> { contentList, note ->
                    NoteViewModel(
                            content = contentList,
                            style = note.style,
                            noteId = note.id
                    )
                })
                .subscribe { viewStatePublisher.onNext(it) }
    }

    private fun createNote() {
        if (viewStatePublisher.hasValue()) {
            return
        }
        val titleContent = Content1()
        titleContent.type = Constants.Frags.TitleFragment.ordinal

        val contentList = listOf(titleContent)

        val updateNote = NoteViewModel(contentList, 0, 0)
        viewStatePublisher.onNext(updateNote)
    }

    fun saveNote() {
        val value = viewStatePublisher.value
        value.content
                ?.let { contentDao.addOrUpdate(it.toTypedArray()) }
    }
}
