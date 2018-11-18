package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Tag
import justbucket.videolib.model.TagPres

fun TagPres.mapToDomain() = Tag(id, text)

fun Tag.mapToPresentation() = TagPres(id, text)