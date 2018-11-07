package justbucket.videolib.viewmodel

import android.content.Context
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.service.ImportService
import justbucket.videolib.state.Resource
import javax.inject.Inject

class ImportViewModel @Inject constructor(getAllTags: GetAllTags)
    : BaseViewModel<List<String>>() {

    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({ liveData.postValue(Resource.success(it)) })
    }

    fun addVideos(context: Context, paths: List<String>, tagPres: List<String>) {
        context.startService(ImportService.newIntent(context, paths, tagPres))
    }


}