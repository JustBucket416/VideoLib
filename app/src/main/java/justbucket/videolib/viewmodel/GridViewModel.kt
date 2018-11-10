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
import justbucket.videolib.utils.createCompletableObserver
import justbucket.videolib.utils.createFlowableSubscriber
import justbucket.videolib.utils.createSingleObserver
import javax.inject.Inject

class GridViewModel @Inject constructor(
        private val getVideos: GetVideos,
        private val getAllTags: GetAllTags,
        private val deleteVideo: DeleteVideo,
        private val addTag: AddTag,
        private val deleteTag: DeleteTag,
        private val loadDetailsState: LoadDetailsState,
        private val saveDetailsState: SaveDetailsState,
        private val loadFilter: LoadFilter,
        private val saveFilter: SaveFilter,
        private val videoMapper: VideoMapper,
        private val filterMapper: FilterMapper) : BaseViewModel<List<VideoPres>>() {

    private val selectedIdsList = ArrayList<Long>()
    private lateinit var lastFilter: FilterPres
    var switchMode = 0
        private set

    override fun onCleared() {
        saveDetailsSwitchState()
        saveFilter.execute(createCompletableObserver { },
                params = SaveFilter.Params.createParams(filterMapper.mapToDomain(lastFilter)
                ))
    }

    fun loadFilter() {
        loadFilter.execute(createSingleObserver({
            lastFilter = filterMapper.mapToPresentation(it)
            fetchVideos(lastFilter)
        }))
    }

    fun saveFilter(filter: FilterPres) {
        saveFilter.execute(
                createCompletableObserver { },
                params = SaveFilter.Params.createParams(filterMapper.mapToDomain(filter)
                ))
    }

    /**
     * Requests videos from domain
     */
    fun fetchVideos(filterPres: FilterPres) {
        getVideos.execute(
                createFlowableSubscriber(liveData) { list ->
                    list.map {
                        val videoPres = videoMapper.mapToPresentation(it)
                        /*if (selectedIdsList.contains(it.id)) {
                            videoPres.selected = true
                            selectedIdsList.remove(it.id)
                        }*/
                        videoPres
                    }
                },
                params = GetVideos.Params.createParams(filterMapper.mapToDomain(filterPres)
                ))
    }

    /**
     * Requests the domain to delete video
     *
     * @param videos - videos to delete
     */
    fun deleteVideos(videos: List<VideoPres>) {
        for ((index, videoPres) in videos.withIndex()) {
            deleteVideo.execute(createCompletableObserver {
                if (index == videos.size - 1) fetchVideos(lastFilter)
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

    fun loadDetailsSwitchState(item: MenuItem) {
        loadDetailsState.execute(createSingleObserver({
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
        }))
    }

    /**
     * Requests the domain to add some new Tags
     *
     * @param tags - the string with tags
     */
    fun addTag(tags: String) {
        tags.split(';').forEach { tag ->
            if (tag.isNotEmpty()) {
                addTag.execute(
                        createCompletableObserver { },
                        params = AddTag.Params.createParams(tag))
            }
        }
    }

    /**
     * Requests the domain to get all tags
     *
     * @param func - the callback function because we don't want callback hell
     */
    fun getAllTags(func: (tags: List<String>) -> Unit) {
        getAllTags.execute(createSingleObserver(func))
    }

    /**
     * Requests the domain to delete some tags
     *
     * @param tags - tags to delete
     */
    fun deleteTags(tags: List<String>) {
        tags.forEachIndexed { index, string ->
            deleteTag.execute(createCompletableObserver {
                lastFilter.tags.remove(string)
                if (index == tags.size - 1) fetchVideos(lastFilter)
            },
                    params = DeleteTag.Params.createParams(string))
        }

    }

    fun savePositions(list: List<VideoPres>) {
        list.forEach {
            if (it.selected) selectedIdsList.add(it.id)
        }
    }

    private fun saveDetailsSwitchState() {
        saveDetailsState.execute(createCompletableObserver { },
                params = SaveDetailsState.Params.createParams(switchMode))
    }

    fun getFilter(): FilterPres {
        return lastFilter
    }

}