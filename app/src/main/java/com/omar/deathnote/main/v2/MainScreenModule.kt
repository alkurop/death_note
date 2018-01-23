package com.omar.deathnote.main.v2

import com.alkurop.database.NoteDao
import dagger.Module
import dagger.Provides

@Module
open class MainScreenModule {
    @MainViewScope
    @Provides
    fun provideMainScreenPresenter(nodeDao: NoteDao): MainViewPresenter {
        return MainViewPresenter(nodeDao)
    }
}
