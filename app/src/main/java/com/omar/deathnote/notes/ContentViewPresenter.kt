package com.omar.deathnote.notes

import com.alkurop.database.Content1
import com.alkurop.database.ContentDao
import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import com.omar.deathnote.Constants
import com.omar.deathnote.utility.plusAssign
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.Date

const val DEFAULT_STYLE = 1

data class NoteViewModel(
        val content: List<Content1>? = null,
        val style: Int = DEFAULT_STYLE,
        val noteId: Long = 0,
        val noteDate: String = ""
)

sealed class ContentAction {
    object CreateNewNote : ContentAction()
    data class OpenNoteById(val id: Long) : ContentAction()
    object FabClicked : ContentAction()
    data class DeleteContent(val id: Long) : ContentAction()
    data class UpdateStyle(val style: Int) : ContentAction()
    data class AddContent(val type: ContentType, val content: String? = null) : ContentAction()
}

sealed class ContentNavigation {
    object ContentSelector : ContentNavigation()
}

class ContentPresenter(
        private val noteDao: NoteDao,
        private val contentDao: ContentDao
) {
    private val dis = CompositeDisposable()
    private val viewStatePublisher = BehaviorSubject.create<NoteViewModel>()

    val navigation = PublishSubject.create<ContentNavigation>()
    val viewState = BehaviorSubject.create<NoteViewModel>()

    init {
        viewStatePublisher
            .scan(NoteViewModel(), { old, new ->
                val noteId = if (new.noteId != 0L) new.noteId else old.noteId
                val content = (new.content ?: old.content)?.map {
                    it.parentNoteId = noteId
                    it
                }

                val noteViewModel = NoteViewModel(
                    content = content,
                    style = if (new.style != 0) new.style else old.style,
                    noteId = noteId,

                    noteDate = new.noteDate.takeIf { it.isNotBlank() } ?: old.noteDate
                )
                noteViewModel
            })
            .distinct()
            .subscribeWith(object : DisposableObserver<NoteViewModel>() {
                override fun onComplete() {
                    viewState.onComplete()
                }

                override fun onNext(t: NoteViewModel) {
                    viewState.onNext(t)
                }

                override fun onError(e: Throwable?) {
                    viewState.onError(e)
                }
            })
    }

    fun dispose() {
        dis.clear()
    }

    fun onAction(action: ContentAction) {
        when (action) {
            is ContentAction.CreateNewNote -> createNote()
            is ContentAction.OpenNoteById -> openNote(action.id)
            is ContentAction.DeleteContent -> deleteContent(action.id)
            is ContentAction.UpdateStyle -> updateNoteStyle(action.style)
            is ContentAction.FabClicked -> navigation.onNext(ContentNavigation.ContentSelector)
            is ContentAction.AddContent -> addContent(action)
        }
    }

    private fun addContent(action: ContentAction.AddContent) {
        val type = action.type.toFragType()
        val content = Content1()
        content.type = type.ordinal
        content.parentNoteId = viewState.value.noteId
        content.content = action.content
        dis += Completable.fromAction { contentDao.addOrUpdate(content) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun deleteContent(id: Long) {
        contentDao.deleteRelatedToNote(id)
    }

    private fun openNote(id: Long) {
        dis += Flowable.combineLatest(
            contentDao.getRelatedToNote(id),
            noteDao.getById(id),
            BiFunction<List<Content1>, Note, NoteViewModel> { contentList, note ->
                NoteViewModel(
                    content = contentList,
                    style = note.style,
                    noteId = note.id,
                    noteDate = note.timedate
                )
            })
            .subscribeOn(Schedulers.io())
            .subscribe { viewStatePublisher.onNext(it) }
    }

    private fun createNote() {
        dis += viewState
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .flatMapObservable { noteViewModel ->
                val note = Note()
                note.id = noteViewModel.noteId
                note.timedate = SimpleDateFormat("dd  MMMM  HH:mm:ss  ").format(Date())
                note.style = noteViewModel.style
                val content = listOf(
                    Content1().apply { type = Constants.Frags.TitleFragment.ordinal },
                    Content1().apply { type = Constants.Frags.NoteFragment.ordinal }
                )

                Single
                    .fromCallable { noteDao.addOrUpdate(note) }
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        noteDao.getById(it)
                            .firstOrError()
                    }.toObservable()
                    .map { NoteViewModel(noteId = it.id, style = it.style, content = content, noteDate = it.timedate) }

            }
            .subscribe {
                viewStatePublisher.onNext(it)
            }
    }

    fun updateNoteStyle(style: Int) {
        dis += viewState
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .flatMapObservable {
                val note = Note()
                note.id = it.noteId
                note.timedate = it.noteDate
                note.style = style

                if (note.id > 0)
                    Single
                        .fromCallable { noteDao.addOrUpdate(note) }
                        .subscribeOn(Schedulers.io())
                        .flatMap {
                            noteDao.getById(it)
                                .firstOrError()
                        }.toObservable()
                else {
                    Observable.just(note)
                }
            }
            .subscribe {
                viewStatePublisher.onNext(NoteViewModel(noteId = it.id, style = it.style, noteDate = it.timedate))
            }
    }
}
