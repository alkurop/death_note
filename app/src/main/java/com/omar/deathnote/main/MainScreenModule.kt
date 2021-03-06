package com.omar.deathnote.main

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import com.omar.deathnote.App
import dagger.Module
import dagger.Provides

@Module
open class MainScreenModule {
    @MainViewScope
    @Provides
    fun provideMainScreenPresenter(noteDao: NoteDao, contentDao: ContentDao, app: App): MainViewPresenter {
        return MainViewPresenter(noteDao, contentDao, app)
    }
}
