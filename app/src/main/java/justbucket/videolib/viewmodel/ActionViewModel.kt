package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.SaveVideoTags
import justbucket.videolib.mapper.VideoMapper
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class ActionViewModel @Inject constructor(getAllTags: GetAllTags,
                                          private val saveVideoTags: SaveVideoTags,
                                          private val videoMapper: VideoMapper)
    : BaseViewModel<List<String>>() {


    init {
        liveData.postValue(Resource.loading())
        getAllTags.execute({
            liveData.postValue(Resource.success(it))
        })
    }

    /**
     * Requests the domain to apply tags to give videos
     *
     * @param items - list of videos to which we apply tags
     * @param tags - a list of tags which we apply to videos
     */
    fun applyTags(items: List<VideoPres>, tags: MutableList<String>) {
        items.filter { it.selected }.forEach {
            it.tags = tags
            saveVideoTags.execute(params = SaveVideoTags.Params.createParams(videoMapper.mapToDomain(it)))
        }
    }

}