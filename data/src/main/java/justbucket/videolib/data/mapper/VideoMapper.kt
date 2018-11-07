package justbucket.videolib.data.mapper

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.domain.model.Video
import javax.inject.Inject

class VideoMapper @Inject constructor(
        private val database: VideoDatabase)
    : Mapper<Video, Pair<VideoEntity, List<String>>> {

    override suspend fun mapToDomain(data: Pair<VideoEntity, List<String>>): Video {
        return Video(data.first.id!!,
                data.first.title,
                data.first.videoPath,
                data.first.thumbPath,
                database.sourceDao().getSourceById(data.first.sourceId),
                data.second)
    }

    override suspend fun mapToData(domain: Video): Pair<VideoEntity, List<String>> {
        return Pair(VideoEntity(domain.id,
                database.sourceDao().getSourceId(domain.source),
                domain.title,
                domain.videoPath,
                domain.thumbPath),
                domain.tags)
    }
}