package justbucket.videolib.data.mapper

import justbucket.videolib.data.model.FilterEntity
import justbucket.videolib.domain.model.Filter

fun FilterEntity.mapToDomain() = Filter(text, sources, allAnyCheck, tags.map { it.mapToDomain() })

fun Filter.mapToData() = FilterEntity(text, sources, allAnyCheck, tags.map { it.mapToData() })