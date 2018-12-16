package justbucket.videolib.data.remote.youtube

import justbucket.videolib.data.remote.youtube.YoutubeConstants.MAX_RESULTS
import justbucket.videolib.data.remote.youtube.YoutubeConstants.PART
import justbucket.videolib.data.remote.youtube.YoutubeConstants.PLAYLIST_URL
import justbucket.videolib.data.remote.youtube.YoutubeConstants.SEARCH_URL
import justbucket.videolib.data.remote.youtube.YoutubeConstants.TOKEN
import justbucket.videolib.data.remote.youtube.YoutubeConstants.VIDEO_URL
import justbucket.videolib.data.remote.youtube.model.playlist.PlaylistRoot
import justbucket.videolib.data.remote.youtube.model.video.VideoRoot
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * A [Retrofit] call interface
 */
interface YoutubeAPI {

    /**
     * Performs an API request to get playlist videos' details
     *
     * @param url - request url
     * @param part - specifies which part of videos' details we want
     * @param results - specifies how much videos we want to get from playlist
     * @param apiKey - an API key for the request
     *
     * @return a Gson skeleton parameterized [Call] instance
     */
    @GET
    fun getPlaylist(@Url url: String = PLAYLIST_URL,
                    @Query("part") part: String = PART,
                    @Query("maxResults") results: Int = MAX_RESULTS,
                    @Query("playlistId") playlist: String,
                    @Query("key") apiKey: String = TOKEN): Call<PlaylistRoot>

    @GET
    fun getNextPage(@Url url: String = PLAYLIST_URL,
                    @Query("part") part: String = PART,
                    @Query("maxResults") results: Int = MAX_RESULTS,
                    @Query("pageToken") pageToken: String,
                    @Query("playlistId") playlist: String,
                    @Query("key") apiKey: String = TOKEN): Call<PlaylistRoot>

    /**
     * Performs an API request to get video details
     *
     * @param url - request url
     * @param part - specifies which part of videos' details we want
     * @param id - specifies video id
     * @param apiKey - an API key for the request
     *
     * @return a Gson skeleton parameterized [Call] instance
     */
    @GET
    fun getVideo(@Url url: String = VIDEO_URL,
                 @Query("part") part: String = PART,
                 @Query("id") id: String,
                 @Query("key") apiKey: String = TOKEN): Call<VideoRoot>

    @GET
    fun getTempVideos(@Url url: String = SEARCH_URL,
                      @Query("part") part: String = PART,
                      @Query("maxResults") maxResults: Int = MAX_RESULTS,
                      @Query("Q") query: String,
                      @Query("key") apiKey: String = TOKEN): Call<VideoRoot>
}