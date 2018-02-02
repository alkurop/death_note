package com.omar.deathnote.notes

import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import com.omar.deathnote.notes.audio.AudioPresenter
import com.omar.deathnote.notes.link.LinkPresenter
import com.omar.deathnote.notes.note.NotePresenter
import com.omar.deathnote.notes.picture.PicturePresenter
import com.omar.deathnote.notes.title.TitlePresenter
import com.omar.deathnote.utility.SharingUtil
import dagger.Module
import dagger.Provides

@Module
open class ContentViewModule {

    @ContentViewScope
    @Provides
    fun provideContentPresenter(
            noteDao: NoteDao,
            contentDao: ContentDao,
            sharingUtil: SharingUtil
    ): ContentPresenter {
        return ContentPresenter(noteDao, contentDao, sharingUtil)
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
