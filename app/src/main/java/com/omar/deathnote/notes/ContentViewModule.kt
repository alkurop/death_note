package com.omar.deathnote.notes

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import com.omar.deathnote.notes.v2.audio.AudioPresenter
import com.omar.deathnote.notes.v2.link.LinkPresenter
import com.omar.deathnote.notes.v2.note.NotePresenter
import com.omar.deathnote.notes.v2.picture.PicturePresenter
import com.omar.deathnote.notes.v2.title.TitlePresenter
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

    @Provides
    fun provideTitlePresenter(contentDao: ContentDao): TitlePresenter {
        return TitlePresenter(contentDao)
    }

    @Provides
    fun provideNotePresenter(contentDao: ContentDao): NotePresenter {
        return NotePresenter(contentDao)
    }

    @Provides
    fun provideAudioPresenter(contentDao: ContentDao): AudioPresenter {
        return AudioPresenter(contentDao)
    }

    @Provides
    fun providePicturePresenter(contentDao: ContentDao): PicturePresenter {
        return PicturePresenter(contentDao)
    }

    @Provides
    fun providesLinkPresenter(contentDao: ContentDao): LinkPresenter {
        return LinkPresenter(contentDao)
    }
}
