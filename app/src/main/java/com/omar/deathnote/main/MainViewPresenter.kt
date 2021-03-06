package com.omar.deathnote.main

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import com.omar.deathnote.App
import com.omar.deathnote.utility.deleteContentFile
import com.omar.deathnote.utility.plusAssign
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

sealed class MainViewActions {
    object FabClicked : MainViewActions()
    object AboutClicked : MainViewActions()
    data class ListItemClicked(val id: Long) : MainViewActions()
    data class DeleteListItemClicked(val id: Long) : MainViewActions()
    data class DeleteListItemConfirmed(val id: Long) : MainViewActions()
    data class SpinnerItemClicked(val style: Int) : MainViewActions()
}

sealed class MainViewNavigation {
    data class NavigateNoteDetails(val id: Long) : MainViewNavigation()
    data class NavigateNewNote(val style: Int) : MainViewNavigation()
    object NavigateAbout : MainViewNavigation()
    data class ConfirmDeleteItem(val noteId: Long) : MainViewNavigation()
}

data class MainViewState(
        val items: List<NoteViewModel>? = null,
        val style: Int? = null
)

data class NoteViewModel(
        val id: Long,
        val style: Int,
        val timedate: String,
        val title: String
)

class MainViewPresenter(
        val noteDao: NoteDao,
        val contentDao: ContentDao,
        val app: App
) {
    companion object {
        private const val DEFAULT_NEW_NOTE_STYLE = 1
    }

    val viewStatePublisher = BehaviorSubject.create<MainViewState>()
    val viewState = viewStatePublisher
        .scan(MainViewState()) { old, new ->
            MainViewState(new.items ?: old.items, new.style ?: old.style)
        }

    val navigation = PublishSubject.create<MainViewNavigation>()
    private val dis = CompositeDisposable()

    fun dispose() {
        dis.clear()
    }

    fun onAction(action: MainViewActions) {
        when (action) {
            MainViewActions.FabClicked -> {
                val style = viewStatePublisher.value?.style ?: DEFAULT_NEW_NOTE_STYLE
                navigation.onNext(MainViewNavigation.NavigateNewNote(style))
            }
            is MainViewActions.ListItemClicked -> navigation.onNext(MainViewNavigation.NavigateNoteDetails(action.id))

            is MainViewActions.DeleteListItemConfirmed -> deleteListItem(action.id)
            is MainViewActions.DeleteListItemClicked -> {
                dis += contentDao.getRelatedToNote(action.id)

                    .subscribe {
                        var doesHaveContent = false
                        it.forEach {
                            val thisItemHasContent = it.content.isNullOrBlank().not()
                            if (thisItemHasContent) {
                                doesHaveContent = true
                            }

                        }
                        if (doesHaveContent) {
                            navigation.onNext(MainViewNavigation.ConfirmDeleteItem(action.id))
                        } else {
                            deleteListItem(action.id)
                        }
                    }
            }
            is MainViewActions.SpinnerItemClicked -> {
                dispose()
                val notesFlowable = if (action.style == 0) {
                    noteDao.getAllNotes()
                } else {
                    noteDao.getNotesByStyle(action.style)
                }
                dis += notesFlowable
                    .switchMap { notes ->
                        val map = notes.map { note ->
                            contentDao.getTitleContent(note.id)
                                .map {
                                    NoteViewModel(
                                        id = note.id,
                                        style = note.style,
                                        timedate = note.timedate,
                                        title = it.content ?: ""
                                    )
                                }
                        }

                        if (map.isEmpty()) {
                            Flowable.just(listOf())
                        } else {
                            Flowable.combineLatest<NoteViewModel,
                                    List<NoteViewModel>>(map, { it.map { it as NoteViewModel } })
                        }
                    }
                    .distinctUntilChanged()
                    .subscribe {
                        updateNotesList(it)
                    }
            }
            MainViewActions.AboutClicked -> {
                navigation.onNext(MainViewNavigation.NavigateAbout)
            }
        }
    }

    fun updateNotesList(notes: List<NoteViewModel>) {
        viewStatePublisher.onNext(MainViewState(notes))
    }

    fun deleteListItem(id: Long) {
        contentDao.getRelatedToNote(id)
            .take(1)
            .toObservable()
            .subscribeOn(io())
            .subscribe {
                it.forEach {
                    contentDao.delete(it.id)
                    it.deleteContentFile()
                }
                noteDao.delete(id)
            }
    }
}
