package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Entity(tableName = "maintab2")
open class Note {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: Int = 0

    var style: Int = 0

    var title: String = ""

    var timedate: String = ""
}


@Dao
interface NoteDao {

    @Query("SELECT * FROM maintab2 WHERE style = :arg0")
    fun getNotesByStyle(style: Int): Flowable<List<Note>>

    @Query("SELECT * FROM maintab2")
    fun getAllNotes(): Flowable<List<Note>>

    @Query("DELETE FROM maintab2 WHERE _id = :arg0")
    fun delete(id: Int)

}
