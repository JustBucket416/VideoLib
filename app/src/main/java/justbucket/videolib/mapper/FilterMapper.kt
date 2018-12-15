package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.model.FilterPres

fun FilterPres.mapToDomain() = Filter(text, sources.map { it.toShort() }, isAllAnyCheck, tags.map { it.mapToDomain() })

fun Filter.mapToPresentation() = FilterPres(text, sources.map { it.toInt() }.toMutableList(), allAnyCheck, tags.map { it.mapToPresentation() }.toMutableList())
