package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Entity(tableName = "maintab2",
        indices = [Index(value = ["id"], unique = true)]
)
open class Note {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var style: Int = 0

    var timedate: String = ""
}

@Dao
interface NoteDao {

    @Query("SELECT * FROM maintab2 WHERE style = :arg0 ORDER BY id DESC")
    fun getNotesByStyle(style: Int): Flowable<List<Note>>

    @Query("SELECT * FROM maintab2 ORDER BY id DESC")
    fun getAllNotes(): Flowable<List<Note>>

    @Query("DELETE FROM maintab2 WHERE id = :arg0")
    fun delete(id: Long)

    @Query("SELECT * FROM maintab2 WHERE id = :arg0")
    fun getById(id: Long): Flowable<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(note: Note): Long
}
