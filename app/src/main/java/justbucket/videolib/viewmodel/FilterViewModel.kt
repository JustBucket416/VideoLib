package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.source.GetAllSources
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.mapper.mapToPresentation
import justbucket.videolib.model.TagPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class FilterViewModel @Inject constructor(
        getAllTags: GetAllTags,
        private val getAllSources: GetAllSources) : BaseViewModel<Pair<List<TagPres>, List<Int>>>() {

    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({ tagList ->
            getAllSources.execute({ sourceList ->
                liveData.postValue(Resource.success(Pair(
                        tagList.map { it.mapToPresentation() },
                        sourceList
                )))
            })
        })
    }
}