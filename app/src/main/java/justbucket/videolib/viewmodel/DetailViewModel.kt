package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.SaveVideoTags
import justbucket.videolib.mapper.VideoMapper
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class DetailViewModel @Inject constructor(getAllTags: GetAllTags,
                                          private val saveVideoTags: SaveVideoTags,
                                          private val videoMapper: VideoMapper) : BaseViewModel<List<String>>() {

    /**
     * Requests domain to load all tags
     */
    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({
            liveData.postValue(Resource.success(it))
        })
    }

    /**
     * Requests domain to save new video tags
     *
     * @param videoPres - a video with tags to aply
     */
    fun saveVideoTags(videoPres: VideoPres) {
        saveVideoTags.execute(params = SaveVideoTags.Params.createParams(
                videoMapper.mapToDomain(videoPres)
        ))
    }
}