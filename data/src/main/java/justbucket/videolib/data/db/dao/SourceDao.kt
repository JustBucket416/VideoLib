package justbucket.videolib.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import justbucket.videolib.data.db.DBConstants
import justbucket.videolib.data.model.SourceEntity

/**
 * A Room [Dao] interface for sources table
 */
@Dao
interface SourceDao {

    @Insert
    fun insertSource(sourceEntity: SourceEntity)

    @Delete
    fun deleteSource(sourceEntity: SourceEntity)

    @Query(DBConstants.QUERY_ALL_SOURCES)
    fun getAllSources(): List<SourceEntity>

    @Query(DBConstants.QUERY_BY_SOURCE_ID)
    fun getSourceById(sourceId: Long): Int

    @Query(DBConstants.QUERY_BY_SOURCE_INT)
    fun getSourceId(source: Int): Long
}