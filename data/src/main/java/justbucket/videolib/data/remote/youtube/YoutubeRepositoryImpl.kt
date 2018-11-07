package justbucket.videolib.data.remote.youtube

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.data.remote.youtube.model.playlist.Item
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import javax.inject.Inject

/**
 * An [YoutubeRepository] implementation
 */
class YoutubeRepositoryImpl @Inject constructor(
        private val retrofitHelper: RetrofitHelper,
        /*private val requestManager: RequestManager,*/
        database: VideoDatabase
) : YoutubeRepository {

    private val videoDao = database.videoDao()
    private val linkDao = database.linkDao()
    private val tagDao = database.tagDao()
    private val entities = ArrayList<VideoEntity>()

    override suspend fun loadPlaylist(link: String, tags: List<String>): Either<Failure, Boolean> {
        val response = retrofitHelper.getYoutubeApi().getPlaylist(playlist = link).execute()
        if (response.isSuccessful) {
            entities.addAll(parseResponse(response.body()?.items!!))
        }
        return if (response.body()?.nextPageToken != null) loadRemaining(
                response.body()?.nextPageToken!!, link, tags, false
        ) else Either.Right(false)
    }

    override suspend fun loadVideo(link: String, tags: List<String>): Either<Failure, Boolean> {
        val response = retrofitHelper.getYoutubeApi().getVideo(id = link).execute()
        if (response.isSuccessful) {
            response.body().run {
                val item = this?.items?.get(0)
                val thumbPath: String = item?.snippet?.thumbnails?.maxres?.url
                        ?: item?.snippet?.thumbnails?.standard?.url
                        ?: item?.snippet?.thumbnails?.medium?.url
                        ?: item?.snippet?.thumbnails?.default?.url!!
                /*requestManager
                        .downloadOnly()
                        .load(thumbPath)*/

                val videoPath = "https://www.youtube.com/watch?v=${item?.id!!}"
                val entity = videoDao.getVideoByPath(videoPath)
                return if (entity == null) {
                    val id = videoDao.insertVideo(VideoEntity(null, 1, item.snippet?.title!!,
                            videoPath, thumbPath))
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(id, tagDao.getTagId(it)))
                    }
                    Either.Right(true)
                } else {
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(entity.id!!, tagDao.getTagId(it)))
                    }
                    Either.Right(false)
                }
            }

        } else return Either.Left(Failure.NetworkFailure)
    }

    private fun parseResponse(items: List<Item>): List<VideoEntity> {
        return items.map { item ->
            val thumbPath: String = item.snippet?.thumbnails?.maxres?.url
                    ?: item.snippet?.thumbnails?.standard?.url
                    ?: item.snippet?.thumbnails?.medium?.url
                    ?: item.snippet?.thumbnails?.default?.url!!
            val videoPath = "https://www.youtube.com/watch?v=${item.snippet?.resourceId?.videoId!!}"
            VideoEntity(null, 1, item.snippet?.title!!,
                    videoPath, thumbPath)
        }
    }

    private tailrec fun loadRemaining(token: String, playlist: String, tags: List<String>, check: Boolean): Either<Failure, Boolean> {
        var check = true
        val response = retrofitHelper.getYoutubeApi().getNextPage(pageToken = token, playlist = playlist).execute()
        entities.addAll(parseResponse(response?.body()?.items!!))
        if (response.body()?.nextPageToken == null) {
            entities.forEach {
                /*requestManager
                        .downloadOnly()
                        .load(thumbPath)
                        .submit()*/
                val entity = videoDao.getVideoByPath(it.videoPath)
                if (entity == null) {
                    val id = videoDao.insertVideo(it)
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(id, tagDao.getTagId(it)))
                    }
                } else {
                    check = false
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(entity.id!!, tagDao.getTagId(it)))
                    }
                }
            }
            return Either.Right(check)
        } else return loadRemaining(response.body()?.nextPageToken!!, playlist, tags, check)
    }
}