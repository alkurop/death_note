package com.omar.deathnote.main.v2

import com.alkurop.database.Note
import com.alkurop.database.NoteDao
import io.reactivex.subjects.PublishSubject

sealed class MainViewActions {
    class FabClicked : MainViewActions()
    class ListItemClicked(val id: Int) : MainViewActions()
    class DeleteListItemClicked(val id: Int) : MainViewActions()
    class SpinnerItemClicked(val id: Int) : MainViewActions()
}

sealed class MainViewNavigation {
    class NavigateNoteDetails(val id: Int) : MainViewNavigation()
    class NavigateNewNote() : MainViewNavigation()
}

sealed class MainViewState {
    class UpdateList(val items:List<Note>) : MainViewState()
}


class MainViewPresenter(val noteDao: NoteDao) {
    val viewState = PublishSubject.create<MainViewState>()
    val navigation = PublishSubject.create<MainViewNavigation>()

    fun onAction(action: MainViewActions) {}
}
