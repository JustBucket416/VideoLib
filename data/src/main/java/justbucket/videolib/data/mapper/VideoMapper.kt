package justbucket.videolib.data.mapper

import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.domain.model.Video

fun VideoEntity.mapToDomain(tags: List<TagEntity>) = Video(id!!, title, videoPath, thumbPath, sourceId, tags.map { it.mapToDomain() })

fun Video.mapToDataEntities() = Pair(VideoEntity(id, source, title, videoPath, thumbPath), tags.map { it.mapToData() })