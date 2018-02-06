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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Content

        if (parentNoteId != other.parentNoteId) return false
        if (id != other.id) return false
        if (type != other.type) return false
        if (content != other.content) return false
        if (additionalContent != other.additionalContent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parentNoteId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + type
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (additionalContent?.hashCode() ?: 0)
        return result
    }
}

@Dao
interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(content: Content): Long

    @Query("Delete from content where id = :arg0")
    fun delete(id: Long)

    @Query("Select * from content where parentNoteId = :arg0")
    fun getRelatedToNote(noteId: Long): Flowable<List<Content>>

    @Query("Select * from content where parentNoteId = :arg0 and type = 0")
    fun getTitleContent(noteId: Long): Flowable<Content>

    @Query("Select * from content where id = :arg0 ")
    fun getById(id: Long): Single<Content>
}
