package com.omar.deathnote.main

import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import com.omar.deathnote.utility.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

sealed class MainViewActions {
    object FabClicked : MainViewActions()
    object AboutClicled : MainViewActions()
    class ListItemClicked(val id: Int) : MainViewActions()
    class DeleteListItemClicked(val id: Int) : MainViewActions()
    class SpinnerItemClicked(val style: Int) : MainViewActions()
}

sealed class MainViewNavigation {
    class NavigateNoteDetails(val id: Int) : MainViewNavigation()
    object NavigateNewNote : MainViewNavigation()
    object NavigateAbout : MainViewNavigation()
}

sealed class MainViewState {
    class UpdateList(val items: List<Note>) : MainViewState()
}

class MainViewPresenter(val noteDao: NoteDao) {
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
                dis += if (action.style == 0) {
                    noteDao.getAllNotes().subscribe {
                        updateNotesList(it)
                    }
                } else {
                    noteDao.getNotesByStyle(action.style).subscribe {
                        updateNotesList(it)
                    }
                }
            }
            MainViewActions.AboutClicled -> {
                navigation.onNext(MainViewNavigation.NavigateAbout)
            }
        }
    }

    fun updateNotesList(notes: List<Note>) {
        viewState.onNext(MainViewState.UpdateList(notes))
    }
}
