package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Video
import justbucket.videolib.model.VideoPres

fun VideoPres.mapToDomain() = Video(id, title, videoPath, thumbPath, source.toShort(), tags.map { it.mapToDomain() })

fun Video.mapToPresentation() = VideoPres(id, title, videoPath, thumbPath, source.toInt(), tags.map { it.mapToPresentation() }.toMutableList())
