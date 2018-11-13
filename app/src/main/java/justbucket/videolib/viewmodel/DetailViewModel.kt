package justbucket.videolib.viewmodel

import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.UpdateVideoTags
import justbucket.videolib.mapper.VideoMapper
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import justbucket.videolib.utils.createCompletableObserver
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class DetailViewModel @Inject constructor(getAllTags: GetAllTags,
                                          private val updateVideoTags: UpdateVideoTags,
                                          private val videoMapper: VideoMapper) : BaseViewModel<List<String>>() {

    /**
     * Requests domain to load all text
     */
    init {
        getAllTags.execute(createSingleObserver(
                { liveData.postValue(Resource.success(it)) },
                { liveData.postValue(Resource.loading()) },
                { liveData.postValue(Resource.error(it)) }
        ))
    }

    /**
     * Requests domain to save new video text
     *
     * @param videoPres - a video with text to aply
     */
    fun saveVideoTags(videoPres: VideoPres) {
        updateVideoTags.execute(
                createCompletableObserver { },
                UpdateVideoTags.Params.createParams(
                        videoMapper.mapToDomain(videoPres)
                ))
    }
}