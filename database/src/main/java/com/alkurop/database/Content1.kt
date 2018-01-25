package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
@Entity(foreignKeys = [(ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parentNoteId") ))],
        tableName = "content",
        indices = [Index(value = ["parentNoteId"], unique = false)]
)
open class Content1 {

    var parentNoteId: Long = 0

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var type: Int = 0

    var content: String? = null

    var additionalContent: String? = null
}

@Dao
interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(content: Content1):Long

    @Query("Delete from content where id = :arg0")
    fun delete(id: Long)

    @Query("delete from content where parentNoteId = :arg0")
    fun deleteRelatedToNote(noteId: Long)

    @Query("Select * from content where id = :arg0")
    fun getRelatedToNote(noteId: Long): Flowable<List<Content1>>

    @Query("Select * from content where parentNoteId = :arg0 and type = 1")
    fun getTitleContent(noteId: Long): Flowable<Content1>
}
