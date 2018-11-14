package justbucket.videolib.data

import android.arch.persistence.room.InvalidationTracker
import android.os.Environment
import justbucket.videolib.data.db.DBConstants
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.FilterMapper
import justbucket.videolib.data.mapper.VideoMapper
import justbucket.videolib.data.model.FilterEntity
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VideoRepositoryImpl @Inject constructor(
        private val videoDatabase: VideoDatabase,
        private val videoMapper: VideoMapper,
        private val filterMapper: FilterMapper,
        private val memoryRepository: MemoryRepository,
        private val youtubeRepository: YoutubeRepository)
    : VideoRepository {

    private val playlistRegex = Regex("youtube.*playlist")
    private val shortPlaylistRegex = Regex("youtu\\.be.*playlist")
    private lateinit var lastObserver: VideoObserver

    override suspend fun addVideo(link: String, tags: List<String>): Either<Failure, Boolean> {
        return when {
            link.contains(Environment.getExternalStorageDirectory().absolutePath) -> {
                memoryRepository.loadFromMemory(link, tags)
            }
            ((link.contains(playlistRegex) || link.contains(shortPlaylistRegex)) && !link.contains("youtubeplaylist")) -> {
                youtubeRepository.loadPlaylist(link.substring(link.lastIndexOf('=') + 1), tags)
            }
            link.contains("www.youtube.com/watch?v=") -> {
                youtubeRepository.loadVideo(link.substring(link.lastIndexOf('=') + 1), tags)
            }
            link.contains("youtu.be/") -> {
                youtubeRepository.loadVideo(link.substring(link.lastIndexOf('/') + 1), tags)
            }
            else -> throw IllegalArgumentException("Unknown link")
        }
    }

    override suspend fun deleteVideo(video: Video) {
        videoDatabase.videoDao().deleteVideo(videoMapper.mapToData(video).first)
    }

    override suspend fun updateVideoTags(video: Video) {
        val linkDao = videoDatabase.linkDao()
        linkDao.deleteAllLinks(video.id)
        video.tags.forEach { linkDao.insertLink(LinkEntity(video.id, videoDatabase.tagDao().getTagId(it))) }
    }

    override suspend fun addVideoTags(video: Video, tags: List<String>) {
        val linkDao = videoDatabase.linkDao()
        tags.forEach {
            linkDao.insertLink(LinkEntity(video.id, videoDatabase.tagDao().getTagId(it)))
        }
    }

    override suspend fun getFilteredVideos(filter: Filter): List<Video> {
        val links = videoDatabase.linkDao().getAllLinks()
        val tags = videoDatabase.tagDao().getAllTags()
        val filterEntity = filterMapper.mapToData(filter)
        val videoEntities = videoDatabase.videoDao().getAllVideos()
        return videoEntities.map { videoEntity ->
            val tagEntities = links.asSequence().filter { it.videoId == videoEntity.id }.map { linkEntity ->
                tags.findLast {
                    linkEntity.tagId == it.id
                }?.text!!
            }.toList()
            videoMapper.mapToDomain(Pair(videoEntity, tagEntities))
        }.filter { checkVideo(filterEntity, it) }
    }

    override suspend fun subscribeToVideos(onNext: (List<Video>) -> Unit, filter: Filter, coroutineContext: CoroutineContext) {
        with(videoDatabase.invalidationTracker) {
            val observer = VideoObserver(filter, onNext, coroutineContext)
            addObserver(observer)
            removeObserver(lastObserver)
            lastObserver = observer
        }
    }

    private fun checkVideo(filter: FilterEntity, video: Video): Boolean {
        val (text, sources, allany, tags) = filter
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