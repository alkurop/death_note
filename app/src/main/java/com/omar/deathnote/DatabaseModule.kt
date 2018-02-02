package com.omar.deathnote

import android.arch.persistence.room.Room
import com.alkurop.database.AppDatabase
import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: App): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "deathnote_room")
            .build()
    }

    @Singleton
    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.getNoteDao()
    }

    @Singleton
    @Provides
    fun provideContentDao(database: AppDatabase): ContentDao {
        return database.getContentDao()
    }
}
