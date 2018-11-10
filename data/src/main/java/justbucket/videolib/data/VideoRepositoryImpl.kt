package justbucket.videolib.data

import android.os.Environment
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.FilterMapper
import justbucket.videolib.data.mapper.VideoMapper
import justbucket.videolib.data.model.FilterEntity
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.utils.getOrDie
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
        private val filterMapper: FilterMapper,
        private val videoDatabase: VideoDatabase,
        private val videoMapper: VideoMapper,
        private val memoryRepository: MemoryRepository,
        private val youtubeRepository: YoutubeRepository)
    : VideoRepository {

    private val playlistRegex = Regex("youtube.*playlist")
    private val shortPlaylistRegex = Regex("youtu\\.be.*playlist")
    private var lastFilter: FilterEntity? = null

    override fun addVideo(link: String, tags: List<String>): Single<Boolean> {
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

    override fun deleteVideo(video: Video): Completable {
        return Completable.defer {
            videoDatabase.videoDao().deleteVideo(videoMapper.mapToData(video).first)
            Completable.complete()
        }
    }

    override fun updateVideoTags(video: Video): Completable {
        return Completable.defer {
            val linkDao = videoDatabase.linkDao()
            linkDao.deleteAllLinks(video.id)
            video.tags.forEach { linkDao.insertLink(LinkEntity(video.id, videoDatabase.tagDao().getTagId(it))) }
            Completable.complete()
        }
    }

    override fun addVideoTags(video: Video, tags: List<String>): Completable {
        return Completable.defer {
            val linkDao = videoDatabase.linkDao()
            tags.forEach {
                linkDao.insertLink(LinkEntity(video.id, videoDatabase.tagDao().getTagId(it)))
            }
            Completable.complete()
        }
    }

    override fun getFilteredVideos(filter: Filter): Flowable<List<Video>> {
        val links = videoDatabase.linkDao().getAllLinks()
        val tags = videoDatabase.tagDao().getAllTags()
        val filterEntity = filterMapper.mapToData(filter)
        val videoEntities = if (filterEntity == lastFilter) {
            videoDatabase.videoDao().getDistinctVideos()
        } else {
            lastFilter = filterEntity
            videoDatabase.videoDao().getAllVideos()
        }

        return videoEntities.map { list ->
            list.map { videoEntity ->

                val tagEntities = links.blockingGet().asSequence().filter { it.videoId == videoEntity.id }.map { linkEntity ->
                    tags.blockingGet().findLast {
                        linkEntity.tagId == it.id
                    }?.text.getOrDie("linked tag")
                }.toList()
                videoMapper.mapToDomain(Pair(videoEntity, tagEntities))
            }.filter {
                checkVideo(filterEntity, it)
            }
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
}