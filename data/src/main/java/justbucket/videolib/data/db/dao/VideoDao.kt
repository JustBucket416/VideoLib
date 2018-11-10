package justbucket.videolib.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import justbucket.videolib.data.db.DBConstants.QUERY_ALL_VIDEOS
import justbucket.videolib.data.db.DBConstants.QUERY_VIDEO_BY_ID
import justbucket.videolib.data.db.DBConstants.QUERY_VIDEO_BY_PATH
import justbucket.videolib.data.model.VideoEntity

/**
 * A Room [Dao] interface for videos table
 */
@Dao
abstract class VideoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertVideo(videoEntity: VideoEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateVideo(videoEntity: VideoEntity)

    @Delete
    abstract fun deleteVideo(videoEntity: VideoEntity)

    @Query(QUERY_ALL_VIDEOS)
    abstract fun getAllVideos(): Flowable<List<VideoEntity>>

    @Query(QUERY_VIDEO_BY_ID)
    abstract fun getVideoById(id: Long): VideoEntity?

    @Query(QUERY_VIDEO_BY_PATH)
    abstract fun getVideoByPath(path: String): VideoEntity?

    fun getDistinctVideos(): Flowable<List<VideoEntity>> {
        return getAllVideos().distinctUntilChanged()
    }

}