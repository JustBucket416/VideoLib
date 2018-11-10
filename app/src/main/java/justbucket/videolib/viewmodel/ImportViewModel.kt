package justbucket.videolib.viewmodel

import android.content.Context
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.service.ImportService
import justbucket.videolib.state.Resource
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class ImportViewModel @Inject constructor(getAllTags: GetAllTags)
    : BaseViewModel<List<String>>() {

    init {
        getAllTags.execute(createSingleObserver(
                { liveData.postValue(Resource.success(it)) },
                { liveData.postValue(Resource.loading()) },
                { liveData.postValue(Resource.error(it)) }
        ))
    }

    fun addVideos(context: Context, paths: List<String>, tagPres: List<String>) {
        context.startService(ImportService.newIntent(context, paths, tagPres))
    }

}