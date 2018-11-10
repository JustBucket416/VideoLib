package justbucket.videolib.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Single
import justbucket.videolib.data.db.DBConstants
import justbucket.videolib.data.db.DBConstants.QUERY_ALL_TAGS
import justbucket.videolib.data.model.TagEntity

/**
 * A Room [Dao] interface for tags table
 */
@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTag(tagEntity: TagEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTag(tagEntity: TagEntity)

    @Delete
    fun deleteTag(tagEntity: TagEntity)

    @Query(QUERY_ALL_TAGS)
    fun getAllTags(): Single<List<TagEntity>>

    @Query(DBConstants.QUERY_TAG_BY_ID)
    fun getTagId(tagText: String): Long

    @Query(DBConstants.QUERY_TAG_BY_TEXT)
    fun findTagByText(tagText: String): TagEntity


}