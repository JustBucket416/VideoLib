package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.model.FilterPres
import javax.inject.Inject

class FilterMapper @Inject constructor() : Mapper<FilterPres, Filter> {

    override fun mapToDomain(presentation: FilterPres): Filter {
        return Filter(presentation.text, presentation.sources, presentation.isAllAnyCheck, presentation.tags)
    }

    override fun mapToPresentation(domain: Filter): FilterPres {
        return FilterPres(domain.text, domain.sources.toMutableList(), domain.allAnyCheck, domain.tags.toMutableList())
    }
}
