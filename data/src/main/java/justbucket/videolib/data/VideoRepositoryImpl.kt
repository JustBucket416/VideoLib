package justbucket.videolib.data

import android.os.Environment
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.VideoMapper
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
        private val videoDatabase: VideoDatabase,
        private val videoMapper: VideoMapper,
        private val memoryRepository: MemoryRepository,
        private val youtubeRepository: YoutubeRepository)
    : VideoRepository {

    private val playlistRegex = Regex("youtube.*playlist")
    private val shortPlaylistRegex = Regex("youtu\\.be.*playlist")

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

    override suspend fun getAllVideos(): List<Video> {
        val links = videoDatabase.linkDao().getAllLinks()
        val tags = videoDatabase.tagDao().getAllTags()
        val videoEntities = videoDatabase.videoDao().getAllVideos()
        return videoEntities.map { videoEntity ->
            val tagEntities = links.asSequence().filter { it.videoId == videoEntity.id }.map { linkEntity ->
                tags.findLast {
                    linkEntity.tagId == it.id
                }?.text!!
            }.toList()
            videoMapper.mapToDomain(Pair(videoEntity, tagEntities))
        }
    }
}