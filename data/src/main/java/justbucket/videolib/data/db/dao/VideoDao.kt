package justbucket.videolib.data.db.dao

import android.arch.persistence.room.*
import justbucket.videolib.data.db.DBConstants.QUERY_ALL_VIDEOS
import justbucket.videolib.data.db.DBConstants.QUERY_VIDEO_BY_ID
import justbucket.videolib.data.db.DBConstants.QUERY_VIDEO_BY_PATH
import justbucket.videolib.data.model.VideoEntity

/**
 * A Room [Dao] interface for videos table
 */
@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVideo(videoEntity: VideoEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVideo(videoEntity: VideoEntity)

    @Delete
    fun deleteVideo(videoEntity: VideoEntity)

    @Query(QUERY_ALL_VIDEOS)
    fun getAllVideos(): List<VideoEntity>

    @Query(QUERY_VIDEO_BY_ID)
    fun getVideoById(id: Long): VideoEntity?

    @Query(QUERY_VIDEO_BY_PATH)
    fun getVideoByPath(path: String): VideoEntity?

}