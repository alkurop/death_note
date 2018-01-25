package com.omar.deathnote.main

import com.alkurop.database.ContentDao
import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import com.omar.deathnote.utility.plusAssign
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

sealed class MainViewActions {
    object FabClicked : MainViewActions()
    object AboutClicked : MainViewActions()
    data class ListItemClicked(val id: Long) : MainViewActions()
    data class DeleteListItemClicked(val id: Long) : MainViewActions()
    data class SpinnerItemClicked(val style: Int) : MainViewActions()
}

sealed class MainViewNavigation {
    data class NavigateNoteDetails(val id: Long) : MainViewNavigation()
    object NavigateNewNote : MainViewNavigation()
    object NavigateAbout : MainViewNavigation()
}

sealed class MainViewState {
    data class UpdateList(val items: List<NoteViewModel>) : MainViewState()
}

data class NoteViewModel(val id: Long,
                         val style: Int,
                         val timedate: String,
                         val title: String)

class MainViewPresenter(val noteDao: NoteDao,
                        val contentDao: ContentDao) {
    val viewState = BehaviorSubject.create<MainViewState>()
    val navigation = PublishSubject.create<MainViewNavigation>()
    private val dis = CompositeDisposable()

    fun dispose() {
        dis.clear()
    }

    fun onAction(action: MainViewActions) {
        when (action) {
            MainViewActions.FabClicked -> navigation.onNext(MainViewNavigation.NavigateNewNote)
            is MainViewActions.ListItemClicked -> navigation.onNext(MainViewNavigation.NavigateNoteDetails(action.id))

            is MainViewActions.DeleteListItemClicked -> {
                noteDao.delete(action.id)
            }
            is MainViewActions.SpinnerItemClicked -> {
                dispose()
                val notesFlowable = if (action.style == 0) {
                    noteDao.getAllNotes()
                } else {
                    noteDao.getNotesByStyle(action.style)
                }
                //todo move this to a use case
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
                                        }.toFlowable()
                            }
                            Flowable.combineLatest<NoteViewModel, List<NoteViewModel>>(map, { it.map { it as NoteViewModel } })
                        }
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
        viewState.onNext(MainViewState.UpdateList(notes))
    }
}
