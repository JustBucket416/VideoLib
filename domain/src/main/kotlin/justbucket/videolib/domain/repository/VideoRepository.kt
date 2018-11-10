package justbucket.videolib.domain.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video

/**
 * An video repository interface
 */
interface VideoRepository {

    /**
     * Adds video to the db
     *
     * @param link - video link
     * @param tags - video tags
     *
     * @return an [Either] instance
     */
    fun addVideo(link: String, tags: List<String>): Single<Boolean>

    /**
     * Deletes a video from the db
     *
     * @param video - a video to delete
     */
    fun deleteVideo(video: Video): Completable

    /**
     * Updates video's tags
     *
     * @param video - a video instance
     * @param tags - a new list of video tags
     */
    fun updateVideoTags(video: Video): Completable

    /**
     * Adds tags to tags that are already stored
     *
     * @param video - a video instance
     * @param tags - a list of tags to add
     */
    fun addVideoTags(video: Video, tags: List<String>): Completable

    /**
     * Loads all videos from the db
     *
     * @return a list of [Video] instances
     */
    fun getFilteredVideos(filter: Filter): Flowable<List<Video>>

}