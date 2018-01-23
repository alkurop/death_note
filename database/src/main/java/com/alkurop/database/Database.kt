package com.alkurop.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(entities = [(Note::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
}
