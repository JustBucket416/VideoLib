package justbucket.videolib.data.mapper

import justbucket.videolib.data.model.FilterEntity
import justbucket.videolib.domain.model.Filter
import javax.inject.Inject

class FilterMapper @Inject constructor() : Mapper<Filter, FilterEntity> {

    override suspend fun mapToDomain(data: FilterEntity): Filter {
        return Filter(data.text,
                data.sources,
                data.allAnyCheck,
                data.tags)
    }

    override suspend fun mapToData(domain: Filter): FilterEntity {
        return FilterEntity(domain.text,
                domain.sources,
                domain.allAnyCheck,
                domain.tags)
    }
}