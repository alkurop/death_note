package com.alkurop.database

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Entity(
    tableName = "link"
)
open class Link {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var path: String? = null

    var content: String? = null

    var timeStamp: Long? = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Link

        if (id != other.id) return false
        if (path != other.path) return false
        if (content != other.content) return false
        if (timeStamp != other.timeStamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (timeStamp?.hashCode() ?: 0)
        return result
    }
}

@Dao
interface LinkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(link: Link): Long

    @Query("Delete from link where id = :id")
    fun delete(id: Long)

    @Query("Select * from link where path = :path ")
    fun getByPath(path: String): Maybe<Link>
}
