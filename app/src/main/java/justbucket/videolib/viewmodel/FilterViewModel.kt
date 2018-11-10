package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.source.GetAllSources
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.state.Resource
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class FilterViewModel @Inject constructor(
        getAllTags: GetAllTags,
        private val getAllSources: GetAllSources) : BaseViewModel<Pair<List<String>, List<Int>>>() {

    init {
        getAllTags.execute(createSingleObserver(
                { tags ->
                    getAllSources.execute(createSingleObserver(
                            { sources ->
                                liveData.postValue(Resource.success(Pair(tags,
                                        sources)))

                            }
                    ))

                }
        ))
    }

}