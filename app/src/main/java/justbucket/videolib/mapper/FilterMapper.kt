package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.model.FilterPres

fun FilterPres.mapToDomain() = Filter(text, sources, isAllAnyCheck, tags.map { it.mapToDomain() })

fun Filter.mapToPresentation() = FilterPres(text, sources.toMutableList(), allAnyCheck, tags.map { it.mapToPresentation() }.toMutableList())
