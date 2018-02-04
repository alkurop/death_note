package com.omar.deathnote.notes

import com.alkurop.database.Content
import com.alkurop.database.ContentDao
import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import com.omar.deathnote.Constants
import com.omar.deathnote.notes.content.ContentType
import com.omar.deathnote.notes.content.toFragType
import com.omar.deathnote.utility.SharingUtil
import com.omar.deathnote.utility.plusAssign
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

const val DEFAULT_STYLE = 1

data class NoteViewModel(
        val content: List<Content>? = null,
        val style: Int = DEFAULT_STYLE,
        val noteId: Long = 0,
        val noteDate: String = ""
)

sealed class ContentAction {
    object CreateNewNote : ContentAction()
    data class OpenNoteById(val id: Long) : ContentAction()
    object FabClicked : ContentAction()
    data class DeleteContent(val id: Long) : ContentAction()
    data class DeleteContentConfirmed(val id: Long) : ContentAction()
    data class UpdateStyle(val style: Int) : ContentAction()
    data class AddContent(val type: ContentType, val content: String? = null) : ContentAction()
    object ShareClicked : ContentAction()
}

sealed class ContentNavigation {
    object ContentSelector : ContentNavigation()
    data class ConfirmDeleteContent(val id: Long) : ContentNavigation()
}

class ContentPresenter @Inject constructor(
        private val noteDao: NoteDao,
        private val contentDao: ContentDao,
        private val sharingUtil: SharingUtil
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
            is ContentAction.DeleteContentConfirmed -> deleteContent(action.id)
            is ContentAction.DeleteContent -> {
                dis += contentDao.getById(action.id)
                    .subscribeOn(io())
                    .toObservable()
                    .subscribe {
                        if (it.content.isNullOrBlank()) {
                            deleteContent(action.id)
                        } else {
                            navigation.onNext(ContentNavigation.ConfirmDeleteContent(action.id))
                        }
                    }
            }
            is ContentAction.UpdateStyle -> updateNoteStyle(action.style)
            is ContentAction.FabClicked -> navigation.onNext(ContentNavigation.ContentSelector)
            is ContentAction.AddContent -> addContent(action)
            is ContentAction.ShareClicked -> share()
        }
    }

    private fun share() {
        dis += sharingUtil
            .share(viewState.value.noteId)
            .subscribe()
    }

    private fun addContent(action: ContentAction.AddContent) {
        val type = action.type.toFragType()
        val content = Content()
        content.type = type.ordinal
        content.parentNoteId = viewState.value.noteId
        content.content = action.content
        dis += Completable.fromAction { contentDao.addOrUpdate(content) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun deleteContent(id: Long) {
        dis += contentDao.getById(id)
            .subscribeOn(io())
            .toObservable()
            .subscribe {
                if (it.type == Constants.Frags.PicFragment.ordinal) {
                    File(it.content).delete()
                }
                contentDao.delete(id)
            }
    }

    private fun openNote(id: Long) {
        dis += Flowable.combineLatest(
            contentDao.getRelatedToNote(id),
            noteDao.getById(id),
            BiFunction<List<Content>, Note, NoteViewModel> { contentList, note ->
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

                Single
                    .fromCallable { noteDao.addOrUpdate(note) }
                    .doOnSuccess {
                        listOf(
                            Content().apply {
                                type = Constants.Frags.TitleFragment.ordinal
                                parentNoteId = it
                            },
                            Content().apply {
                                type = Constants.Frags.NoteFragment.ordinal
                                parentNoteId = it
                            }
                        ).forEach {
                            contentDao.addOrUpdate(it)
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .toObservable()

            }
            .subscribe {
                openNote(it)
            }
    }

    fun updateNoteStyle(style: Int) {
        dis += viewState
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .flatMapCompletable {
                val note = Note()
                note.id = it.noteId
                note.timedate = it.noteDate
                note.style = style

                if (note.id > 0)
                    Single
                        .fromCallable { noteDao.addOrUpdate(note) }
                        .subscribeOn(Schedulers.io())
                        .toCompletable()
                else {
                    Completable.complete()
                }
            }
            .subscribe()
    }
}
