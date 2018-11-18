package justbucket.videolib.viewmodel

import android.content.Context
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.mapper.mapToPresentation
import justbucket.videolib.model.TagPres
import justbucket.videolib.service.ImportService
import justbucket.videolib.state.Resource
import javax.inject.Inject

class ImportViewModel @Inject constructor(getAllTags: GetAllTags)
    : BaseViewModel<List<TagPres>>() {

    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({ list -> liveData.postValue(Resource.success(list.map { it.mapToPresentation() })) })
    }

    fun addVideos(context: Context, paths: List<String>, tags: ArrayList<TagPres>) {
        context.startService(ImportService.newIntent(context, paths, tags))
    }


}