package com.alkurop.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(
        entities = [Note::class, Content::class],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    abstract fun getContentDao(): ContentDao
}
