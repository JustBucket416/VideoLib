package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.SaveVideoTags
import justbucket.videolib.mapper.mapToDomain
import justbucket.videolib.mapper.mapToPresentation
import justbucket.videolib.model.TagPres
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class DetailViewModel @Inject constructor(getAllTags: GetAllTags,
                                          private val saveVideoTags: SaveVideoTags) : BaseViewModel<List<TagPres>>() {

    /**
     * Requests domain to load all tags
     */
    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({ list ->
            liveData.postValue(Resource.success(list.map { it.mapToPresentation() }))
        })
    }

    /**
     * Requests domain to save new video tags
     *
     * @param videoPres - a video with tags to aply
     */
    fun saveVideoTags(videoPres: VideoPres) {
        saveVideoTags.execute(params = SaveVideoTags.Params.createParams(
                videoPres.mapToDomain()
        ))
    }
}