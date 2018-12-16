package justbucket.videolib.domain.repository

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.model.Video
import kotlin.coroutines.CoroutineContext

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
    suspend fun addVideo(link: String, tags: List<Tag>): Either<Failure, Boolean>

    /**
     * Deletes a video from the db
     *
     * @param video - a video to delete
     */
    suspend fun deleteVideo(video: Video)

    /**
     * Updates video's tags
     *
     * @param video - a video instance
     * @param tags - a new list of video tags
     */
    suspend fun updateVideoTags(video: Video)

    /**
     * Adds tags to tags that are already stored
     *
     * @param video - a video instance
     * @param tags - a list of tags to add
     */
    suspend fun addVideoTags(video: Video, tags: List<Tag>)

    /**
     * Loads all videos from the db
     *
     * @return a list of [Video] instances
     */
    suspend fun getFilteredVideos(filter: Filter): List<Video>

    suspend fun subscribeToVideos(onNext: (List<Video>) -> Unit, filter: Filter, coroutineContext: CoroutineContext)

    suspend fun loadVideosByTempTag(text: String): Either<Failure, List<Video>>

}