package justbucket.videolib.data.remote

import io.reactivex.Single

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
    fun loadPlaylist(link: String, tags: List<String>): Single<Boolean>


    /**
     * Loads a single video by given link
     *
     * @param link - video link
     *
     * @return a video entity
     */
    fun loadVideo(link: String, tags: List<String>): Single<Boolean>
}