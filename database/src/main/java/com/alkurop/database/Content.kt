package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Entity(
    foreignKeys = [(ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parentNoteId")
    ))],
    tableName = "content",
    indices = [Index(value = ["parentNoteId"], unique = false)]
)
open class Content {

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
    fun addOrUpdate(content: Content): Long

    @Query("Delete from content where id = :arg0")
    fun delete(id: Long)

    @Query("delete from content where parentNoteId = :arg0")
    fun deleteRelatedToNote(noteId: Long)

    @Query("Select * from content where parentNoteId = :arg0")
    fun getRelatedToNote(noteId: Long): Flowable<List<Content>>

    @Query("Select * from content where parentNoteId = :arg0 and type = 0")
    fun getTitleContent(noteId: Long): Flowable<Content>

    @Query("Select * from content where id = :arg0 ")
    fun getById(id: Long): Single<Content>
}
