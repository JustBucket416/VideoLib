package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.UpdateVideoTags
import justbucket.videolib.mapper.VideoMapper
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import justbucket.videolib.utils.createCompletableObserver
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class ActionViewModel @Inject constructor(getAllTags: GetAllTags,
                                          private val updateVideoTags: UpdateVideoTags,
                                          private val videoMapper: VideoMapper)
    : BaseViewModel<List<String>>() {


    init {
        getAllTags.execute(createSingleObserver(
                { liveData.postValue(Resource.success(it)) },
                { liveData.postValue(Resource.loading()) }
        ))
    }

    /**
     * Requests the domain to apply tags to given videos
     *
     * @param items - list of videos to which we apply tags
     * @param tags - a list of tags which we apply to videos
     */
    fun applyTags(items: List<VideoPres>, tags: MutableList<String>) {
        items.filter { it.selected }.forEach {
            it.tags = tags
            updateVideoTags.execute(
                    createCompletableObserver { },
                    UpdateVideoTags.Params.createParams(
                            videoMapper.mapToDomain(it)
                    ))
        }
    }

}