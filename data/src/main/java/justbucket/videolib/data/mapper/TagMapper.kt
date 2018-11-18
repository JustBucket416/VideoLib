package justbucket.videolib.data.mapper

import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.domain.model.Tag

fun Tag.mapToData() = TagEntity(id, text)

fun TagEntity.mapToDomain() = Tag(id!!, text)