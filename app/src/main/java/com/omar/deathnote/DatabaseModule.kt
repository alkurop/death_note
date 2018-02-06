package com.omar.deathnote

import android.arch.persistence.room.Room
import com.alkurop.database.AppDatabase
import com.alkurop.database.ContentDao
import com.alkurop.database.NoteDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.alkurop.database.LinkDao


@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: App): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "deathnote_room")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE link (timeStamp INTEGER, path TEXT, id INTEGER NOT NULL, content TEXT, PRIMARY KEY(id))")
        }
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

    @Singleton
    @Provides
    fun provideLinkDao(database: AppDatabase): LinkDao {
        return database.getLinkDao()
    }
}
