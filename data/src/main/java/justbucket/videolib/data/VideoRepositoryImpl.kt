package justbucket.videolib.data

import android.arch.persistence.room.InvalidationTracker
import android.os.Environment
import justbucket.videolib.data.db.DBConstants
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.mapToData
import justbucket.videolib.data.mapper.mapToDataEntities
import justbucket.videolib.data.mapper.mapToDomain
import justbucket.videolib.data.model.FilterEntity
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.utils.getOrDie
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VideoRepositoryImpl @Inject constructor(
        private val videoDatabase: VideoDatabase,
        private val memoryRepository: MemoryRepository,
        private val youtubeRepository: YoutubeRepository)
    : VideoRepository {

    private val playlistRegex = Regex("youtube.*playlist")
    private val shortPlaylistRegex = Regex("youtu\\.be.*playlist")
    private lateinit var lastObserver: VideoObserver

    override suspend fun addVideo(link: String, tags: List<Tag>): Either<Failure, Boolean> {
        val entities = tags.map { it.mapToData() }
        return when {
            link.contains(Environment.getExternalStorageDirectory().absolutePath) -> {
                memoryRepository.loadFromMemory(link, entities)
            }
            ((link.contains(playlistRegex) || link.contains(shortPlaylistRegex)) && !link.contains("youtubeplaylist")) -> {
                youtubeRepository.loadPlaylist(link.substring(link.lastIndexOf('=') + 1), entities)
            }
            link.contains("www.youtube.com/watch?v=") -> {
                youtubeRepository.loadVideo(link.substring(link.lastIndexOf('=') + 1), entities)
            }
            link.contains("youtu.be/") -> {
                youtubeRepository.loadVideo(link.substring(link.lastIndexOf('/') + 1), entities)
            }
            else -> throw IllegalArgumentException("Unknown link")
        }
    }

    override suspend fun deleteVideo(video: Video) {
        videoDatabase.videoDao().deleteVideo(video.mapToDataEntities().first)
    }

    override suspend fun updateVideoTags(video: Video) {
        val linkDao = videoDatabase.linkDao()
        val tagEntities = video.tags.map { it.mapToData() }
        linkDao.deleteAllLinks(video.id)
        tagEntities.forEach { linkDao.insertLink(LinkEntity(video.id, it.id!!)) }
    }

    override suspend fun addVideoTags(video: Video, tags: List<Tag>) {
        val linkDao = videoDatabase.linkDao()
        tags.forEach {
            linkDao.insertLink(LinkEntity(video.id, it.id))
        }
    }

    override suspend fun getFilteredVideos(filter: Filter): List<Video> {
        val links = videoDatabase.linkDao().getAllLinks()
        val tags = videoDatabase.tagDao().getAllTags()
        val filterEntity = filter.mapToData()
        val videoEntities = videoDatabase.videoDao().getAllVideos()
        return videoEntities.map { videoEntity ->
            val tagEntities = links.asSequence().filter { it.videoId == videoEntity.id }.map { linkEntity ->
                tags.find {
                    linkEntity.tagId == it.id
                }.getOrDie("tag text")
            }.toList()
            videoEntity.mapToDomain(tagEntities)
        }.filter { checkVideo(filterEntity, it) }
    }

    override suspend fun subscribeToVideos(onNext: (List<Video>) -> Unit, filter: Filter, coroutineContext: CoroutineContext) =
        with(videoDatabase.invalidationTracker) {
            val observer = VideoObserver(filter, onNext, coroutineContext)
            addObserver(observer)
            removeObserver(lastObserver)
            lastObserver = observer
        }

    override suspend fun loadVideosByTempTag(text: String): Either<Failure, List<Video>> {
        return youtubeRepository.loadTempVideos(text)
    }

    private fun checkVideo(filter: FilterEntity, video: Video): Boolean {
        val (text, sources, allany, tags) = filter.mapToDomain()
        if (text.isNotEmpty()) {
            if (!video.title.contains(text)) return false
        }
        if (sources.isNotEmpty()) {
            if (!sources.contains(video.source)) return false
        }
        if (tags.isNotEmpty()) {
            if (allany) {
                tags.forEach {
                    if (!video.tags.contains(it)) return false
                }
            } else {
                if (!tags.any { tag -> video.tags.contains(tag) }) return false
            }
        }
        return true
    }

    override suspend fun saveVideo(video: Video) {
        val (videoEntity, tags) = video.mapToDataEntities()
        val id = videoDatabase.videoDao().insertVideo(VideoEntity(null, videoEntity.sourceId, videoEntity.title, videoEntity.videoPath, videoEntity.thumbPath))
        tags.forEach {
            videoDatabase.linkDao().insertLink(LinkEntity(id, it.id!!))
        }
    }

    private inner class VideoObserver(private val filter: Filter,
                                      private val onNext: (List<Video>) -> Unit,
                                      private val coroutineContext: CoroutineContext)
        : InvalidationTracker.Observer(arrayOf(DBConstants.TABLE_VIDEO_NAME,
            DBConstants.TABLE_LINK_NAME,
            DBConstants.TABLE_TAG_NAME)) {

        override fun onInvalidated(tables: MutableSet<String>) {
            val job = CoroutineScope(Dispatchers.Default).async {
                getFilteredVideos(filter)
            }
            if (!videoDatabase.inTransaction()) {
                GlobalScope.launch(coroutineContext) { onNext(job.await()) }
            }
        }
    }
}