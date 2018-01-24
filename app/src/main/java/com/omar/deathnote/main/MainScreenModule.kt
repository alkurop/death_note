package com.omar.deathnote.main

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import dagger.Module
import dagger.Provides

@Module
open class MainScreenModule {
    @MainViewScope
    @Provides
    fun provideMainScreenPresenter(noteDao: NoteDao, contentDao: ContentDao): MainViewPresenter {
        return MainViewPresenter(noteDao, contentDao)
    }
}
