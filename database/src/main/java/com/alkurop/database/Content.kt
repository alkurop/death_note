package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Entity(foreignKeys = [(ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parentNoteId")
))], tableName = "content")
open class Content {

    var parentNoteId: Long = 0

    @PrimaryKey
    var id: Long = 0

    var type: Int = 0

    var content: String? = null

    var additionalContent: String? = null
}

@Dao
interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(content: Content)

    @Query("Delete from content where id = :arg0")
    fun delete(id: Long)

    @Query("delete from content where parentNoteId = :arg0")
    fun deleteRelatedToNote(noteId: Long)

    @Query("Select * from content where id = :arg0")
    fun getRelatedToNote(noteId: Long): Flowable<List<Content>>

    @Query("Select * from content where parentNoteId = :arg0 and type = 1")
    fun getTitleContent(noteId: Long): Single<Content>
}
