package justbucket.videolib.data.remote.youtube

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.data.remote.youtube.model.playlist.Item
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.utils.getOrDie
import javax.inject.Inject

/**
 * An [YoutubeRepository] implementation
 */
class YoutubeRepositoryImpl @Inject constructor(
        private val youtubeAPI: YoutubeAPI,
        database: VideoDatabase
) : YoutubeRepository {

    private val videoDao = database.videoDao()
    private val linkDao = database.linkDao()

    override suspend fun loadPlaylist(link: String, tags: List<TagEntity>): Either<Failure, Boolean> {
        val response = youtubeAPI.getPlaylist(playlist = link).execute()
        return if (response.isSuccessful) {
            Either.Right(parseItems(response.body()?.nextPageToken,
                    link,
                    tags,
                    response.body()?.items.getOrDie("items"),
                    true))
        } else Either.Left(Failure.NetworkFailure)
    }

    override suspend fun loadVideo(link: String, tags: List<TagEntity>): Either<Failure, Boolean> {
        val response = youtubeAPI.getVideo(id = link).execute()
        if (response.isSuccessful) {
            response.body().run {
                val item = this?.items?.get(0)
                val thumbPath: String = item?.snippet?.thumbnails?.maxres?.url
                        ?: item?.snippet?.thumbnails?.standard?.url
                        ?: item?.snippet?.thumbnails?.medium?.url
                        ?: item?.snippet?.thumbnails?.default?.url!!

                val videoPath = "https://www.youtube.com/watch?v=${item?.id!!}"
                val entity = videoDao.getVideoByPath(videoPath)
                return if (entity == null) {
                    val id = videoDao.insertVideo(VideoEntity(null, 1, item.snippet.title,
                            videoPath, thumbPath))
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(id, it.id!!))
                    }
                    Either.Right(true)
                } else {
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(entity.id!!, it.id!!))
                    }
                    Either.Right(false)
                }
            }

        } else return Either.Left(Failure.NetworkFailure)
    }

    override suspend fun loadTempVideos(text: String): Either<Failure, List<Video>> {
        val response = youtubeAPI.getTempVideos(query = text).execute()
        return if (response.isSuccessful) {
            Either.Right(response.body()?.items?.map { Video(-1,
                    it.snippet.title,
                    "https://www.youtube.com/watch?v=${it.id.getOrDie("videoId")}",
                    it.snippet.thumbnails.medium.url,
                    1,
                    emptyList()) }!!)
        } else Either.Left(Failure.NetworkFailure)
    }

    private tailrec fun parseItems(token: String?, link: String, tags: List<TagEntity>, items: List<Item>, check: Boolean): Boolean {
        var innerCheck = check
        items.forEach { item ->
            val thumbPath: String = item.snippet.thumbnails.maxres?.url
                    ?: item.snippet.thumbnails.standard?.url
                    ?: item.snippet.thumbnails.medium?.url
                    ?: item.snippet.thumbnails.default?.url.getOrDie("image")

            val videoPath = "https://www.youtube.com/watch?v=${item.snippet.resourceId.videoId.getOrDie("videoId")}"
            val entity = videoDao.getVideoByPath(videoPath)
            if (entity == null) {
                val id = videoDao.insertVideo(VideoEntity(null,
                        1,
                        item.snippet.title.getOrDie("title"),
                        videoPath,
                        thumbPath))
                tags.forEach {
                    linkDao.insertLink(LinkEntity(id, it.id!!))
                }
                innerCheck = true
            } else {
                tags.forEach {
                    linkDao.insertLink(LinkEntity(entity.id!!, it.id!!))
                }
                innerCheck = false
            }
        }
        return if (token == null) innerCheck
        else {
            val response = youtubeAPI.getNextPage(pageToken = token, playlist = link).execute()
            parseItems(response.body()?.nextPageToken,
                    link,
                    tags,
                    response.body()?.items.getOrDie("items"),
                    innerCheck)
        }
    }
}