package justbucket.videolib.data.db.dao

import android.arch.persistence.room.*
import justbucket.videolib.data.db.DBConstants.QUERY_DELETE_ALL_LINKS
import justbucket.videolib.data.db.DBConstants.QUERY_GET_ALL_LINKS
import justbucket.videolib.data.model.LinkEntity

/**
 * A Room [Dao] interface for links table
 */
@Dao
interface LinkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLink(linkEntity: LinkEntity)

    @Delete
    fun deleteLink(linkEntity: LinkEntity)

    @Query(QUERY_GET_ALL_LINKS)
    fun getAllLinks(): List<LinkEntity>

    @Query(QUERY_DELETE_ALL_LINKS)
    fun deleteAllLinks(videoId: Long)
}