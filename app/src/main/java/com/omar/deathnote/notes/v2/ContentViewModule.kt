package com.omar.deathnote.notes.v2

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import dagger.Module
import dagger.Provides

@Module
open class ContentViewModule {

    @ContentViewScope
    @Provides
    fun provideContentPresenter(noteDao: NoteDao,
                                contentDao: ContentDao): ContentPresenter {
        return ContentPresenter(noteDao, contentDao)
    }
}
