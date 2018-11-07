package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.preferences.LoadFilter
import justbucket.videolib.domain.feature.source.GetAllSources
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.mapper.FilterMapper
import justbucket.videolib.model.FilterPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class FilterViewModel @Inject constructor(
        getAllTags: GetAllTags,
        private val getAllSources: GetAllSources,
        private val loadFilter: LoadFilter,
        private val filterMapper: FilterMapper) : BaseViewModel<Triple<List<String>, List<Int>, FilterPres>>() {

    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({ tagList ->
            getAllSources.execute({ sourceList ->
                loadFilter.execute({ filterTemplate ->
                    liveData.postValue(Resource.success(Triple(
                            tagList,
                            sourceList,
                            filterMapper.mapToPresentation(filterTemplate)
                    )))
                })
            })
        })
    }
}