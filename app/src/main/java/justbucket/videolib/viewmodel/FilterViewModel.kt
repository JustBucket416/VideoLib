package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.preferences.LoadFilter
import justbucket.videolib.domain.feature.source.GetAllSources
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.mapper.FilterMapper
import justbucket.videolib.model.FilterPres
import justbucket.videolib.state.Resource
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class FilterViewModel @Inject constructor(
        getAllTags: GetAllTags,
        private val getAllSources: GetAllSources,
        private val loadFilter: LoadFilter,
        private val filterMapper: FilterMapper) : BaseViewModel<Triple<List<String>, List<Int>, FilterPres>>() {

    init {
        getAllTags.execute(createSingleObserver(
                { tags ->
                    getAllSources.execute(createSingleObserver(
                            { sources ->
                                loadFilter.execute(createSingleObserver(
                                        {
                                            liveData.postValue(Resource.success(Triple(tags,
                                                    sources,
                                                    filterMapper.mapToPresentation(it))))
                                        }
                                ))

                            }
                    ))

                }
        ))
    }

}