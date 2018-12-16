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
import justbucket.videolib.domain.feature.video.GetTempVideos
import justbucket.videolib.domain.feature.video.GetVideos
import justbucket.videolib.domain.feature.video.SubscribeToVideos
import justbucket.videolib.domain.model.SwitchValues
import justbucket.videolib.domain.model.Video
import justbucket.videolib.mapper.mapToDomain
import justbucket.videolib.mapper.mapToPresentation
import justbucket.videolib.model.FilterPres
import justbucket.videolib.model.TagPres
import justbucket.videolib.model.VideoPres
import justbucket.videolib.state.Resource
import javax.inject.Inject

class GridViewModel @Inject constructor(
        private val subscribeToVideos: SubscribeToVideos,
        private val getVideos: GetVideos,
        private val getTempVideos: GetTempVideos,
        private val getAllTags: GetAllTags,
        private val deleteVideo: DeleteVideo,
        private val addTag: AddTag,
        private val deleteTag: DeleteTag,
        private val loadDetailsState: LoadDetailsState,
        private val saveDetailsState: SaveDetailsState,
        private val loadFilter: LoadFilter,
        private val saveFilter: SaveFilter) : BaseViewModel<List<VideoPres>>() {

    private lateinit var lastFilter: FilterPres
    var switchMode = 0
        private set
    private val unit: (List<Video>) -> Unit = { videoList ->
        liveData.postValue(Resource.success(videoList.map { it.mapToPresentation() }))
    }

    override fun onCleared() {
        saveDetailsSwitchState()
        if (!lastFilter.tags.any { it.id == -1L }) {
            saveFilter.execute(params = SaveFilter.Params.createParams(lastFilter.mapToDomain()))
        }
    }

    fun loadFilter() {
        loadFilter.execute({
            lastFilter = it.mapToPresentation()
            fetchVideos()
        })
    }

    fun setTextTag(tag: String) {
        liveData.postValue(Resource.loading())
        getTempVideos.execute(onResult = { either ->
            either.either({
                liveData.postValue(Resource.error("Failed to load data"))
            },
                    { list ->
                        liveData.postValue(Resource.success(list.map { it.mapToPresentation() }))
                    })
        }, params = GetTempVideos.Params.createParams(tag))
    }

    fun saveFilter(filter: FilterPres) {
        saveFilter.execute(params = SaveFilter.Params.createParams(filter.mapToDomain()))
    }

    /**
     * Requests videos from domain
     */
    fun fetchVideos() {
        liveData.postValue(Resource.loading())
        getVideos.execute(onResult = { list ->
            unit(list)
            subscribeToVideos.execute(params = SubscribeToVideos.Params.createParams(unit, lastFilter.mapToDomain()))
        },
                params = GetVideos.Params.createParams(lastFilter.mapToDomain())
        )
    }

    /**
     * Requests the domain to delete video
     *
     * @param videos - videos to delete
     */
    fun deleteVideos(videos: List<VideoPres>) {
        for ((index, videoPres) in videos.withIndex()) {
            deleteVideo.execute({
                if (index == videos.size - 1) fetchVideos()
            }, DeleteVideo.Params.createParams(videoPres.mapToDomain()))
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
    fun getAllTags(func: (tags: List<TagPres>) -> Unit) {
        getAllTags.execute(onResult = { list ->
            func(list.map {
                it.mapToPresentation()
            })
        })
    }

    /**
     * Requests the domain to delete a tag
     *
     * @param tags - a tag to delete
     */
    fun deleteTags(tags: List<TagPres>) {
        tags.forEachIndexed { index, tagPres ->
            deleteTag.execute(onResult = {
                lastFilter.tags.remove(tagPres)
                if (index == tags.size - 1) fetchVideos()
            },
                    params = DeleteTag.Params.createParams(tagPres.mapToDomain()))
        }

    }

    fun getFilter() = lastFilter

}