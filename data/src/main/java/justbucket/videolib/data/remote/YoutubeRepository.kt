package justbucket.videolib.data.remote

import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either

/**
 * A repository interface which loads videos from YouTube
 */
interface YoutubeRepository {

    /**
     * Loads videos from youtube playlist
     *
     * @param link - playlist link
     *
     * @return a list of video entities
     */
    suspend fun loadPlaylist(link: String, tags: List<TagEntity>): Either<Failure, Boolean>


    /**
     * Loads a single video by given link
     *
     * @param link - video link
     *
     * @return a video entity
     */
    suspend fun loadVideo(link: String, tags: List<TagEntity>): Either<Failure, Boolean>
}