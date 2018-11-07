package justbucket.videolib.viewmodel

import android.view.MenuItem
import justbucket.videolib.R
import justbucket.videolib.domain.feature.preferences.LoadDetailsState
import justbucket.videolib.domain.feature.preferences.LoadFilter
import justbucket.videolib.domain.feature.preferences.SaveDetailsState
import justbucket.videolib.domain.feature.preferences.SaveFilter
import justbucket.videolib.domain.feature.tag.AddTag
import justbucket.videolib.domain.feature.tag.DeleteTag
import justbucket.videolib.domain.feature.tag.GetAllTags
import justbucket.videolib.domain.feature.video.DeleteVideo
import justbucket.videolib.domain.feature.video.GetVideos
import justbucket.videolib.domain.model.SwitchValues
import justbucket.videolib.mapper.FilterMapper
import justbucket.videolib.mapper.VideoMapper
import justbucket.videolib.model.FilterPres
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class GridViewModel @Inject constructor(
        private val getVideos: GetVideos,
        private val getAllTags: GetAllTags,
        private val videoMapper: VideoMapper,
        private val deleteVideo: DeleteVideo,
        private val addTag: AddTag,
        private val deleteTag: DeleteTag,
        private val loadDetailsState: LoadDetailsState,
        private val saveDetailsState: SaveDetailsState,
        private val loadFilter: LoadFilter,
        private val saveFilter: SaveFilter,
        private val filterMapper: FilterMapper) : BaseViewModel<List<VideoPres>>() {

    private lateinit var lastFilter: FilterPres
    var switchMode = 0
        private set

    override fun onCleared() {
        saveDetailsSwitchState()
        saveFilter.execute(params = SaveFilter.Params.createParams(filterMapper.mapToDomain(lastFilter)))
    }

    fun getFilter() {
        loadFilter.execute({
            lastFilter = filterMapper.mapToPresentation(it)
            fetchVideosInternal()
        })
    }

    fun saveFilter(filter: FilterPres) {
        saveFilter.execute(params = SaveFilter.Params.createParams(filterMapper.mapToDomain(filter)))
    }

    /**
     * Requests videos from domain
     */
    fun fetchVideos(filterPres: FilterPres) {
        if (lastFilter == filterPres) return
        lastFilter = filterPres
        fetchVideosInternal()
    }

    /**
     * Requests the domain to delete video
     *
     * @param videos - videos to delete
     */
    fun deleteVideos(videos: List<VideoPres>) {
        for ((index, videoPres) in videos.withIndex()) {
            deleteVideo.execute({
                if (index == videos.size - 1) fetchVideosInternal()
            }, DeleteVideo.Params.createParams(videoMapper.mapToDomain(videoPres)))
        }
    }

    /**
     * Handles the detail mode change
     */
    fun modeChanged(item: MenuItem) {
        switchMode = if (switchMode == 2) 0 else ++switchMode
        when (switchMode) {
            SwitchValues.SWITCH_OPEN_DETAILS -> {
                item.setIcon(R.drawable.arrow_expand_all)
            }
            SwitchValues.SWITCH_PLAY_VIDEO -> {
                item.setIcon(R.drawable.video_clip)
            }
            SwitchValues.SWITCH_PLAY_AUDIO -> {
                item.setIcon(R.drawable.ic_audiotrack)
            }
        }
    }

    private fun saveDetailsSwitchState() {
        saveDetailsState.execute(params = SaveDetailsState.Params.createParams(switchMode))
    }

    fun loadDetailsSwitchState(item: MenuItem) {
        loadDetailsState.execute({
            switchMode = it
            when (switchMode) {
                SwitchValues.SWITCH_OPEN_DETAILS -> {
                    item.setIcon(R.drawable.arrow_expand_all)
                }
                SwitchValues.SWITCH_PLAY_VIDEO -> {
                    item.setIcon(R.drawable.video_clip)
                }
                SwitchValues.SWITCH_PLAY_AUDIO -> {
                    item.setIcon(R.drawable.ic_audiotrack)
                }
            }
        })
    }

    /**
     * Requests the domain to add some new Tags
     *
     * @param tags - the string with tags
     */
    fun addTag(tags: String) {
        tags.split(';').forEach { tag ->
            if (tag.isNotEmpty()) {
                addTag.execute(params = AddTag.Params.createParams(tag))
            }
        }
    }

    /**
     * Requests the domain to get all tags
     *
     * @param func - the callback function because we don't want callback hell
     */
    fun getAllTags(func: (tags: List<String>) -> Unit) {
        getAllTags.execute(func)
    }

    /**
     * Requests the domain to delete a tag
     *
     * @param text - a tag to delete
     */
    fun deleteTag(text: String) {
        deleteTag.execute(params = DeleteTag.Params.createParams(text))
    }

    private fun fetchVideosInternal() {
        liveData.postValue(Resource.loading())
        getVideos.execute(onResult = { list ->
            liveData.postValue(Resource.success(list.map { videoMapper.mapToPresentation(it) }))
        },
                params = GetVideos.Params.createParams(filterMapper.mapToDomain(lastFilter)))
    }

}