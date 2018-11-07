package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GetVideos @Inject constructor(
        context: CoroutineContext,
        private val videoRepository: VideoRepository)
    : UseCase<List<Video>, GetVideos.Params>(context) {

    override suspend fun run(params: Params?): List<Video> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        var videos = videoRepository.getAllVideos()
        with(params.filter) {
            if (text.isNotEmpty()) videos = videos.filter { it.title.contains(text) }
            if (sources.isNotEmpty()) videos = videos.filter { sources.contains(it.source) }
            if (tags.isNotEmpty()) {
                videos = if (allAnyCheck) {
                    videos.filter { video ->
                        tags.forEach {
                            if (!video.tags.contains(it)) return@filter false
                        }
                        return@filter true
                    }
                } else {
                    videos.filter { video ->
                        tags.forEach {
                            if (video.tags.contains(it)) return@filter true
                        }
                        return@filter false
                    }
                }
            }
        }
        return videos
    }

    data class Params internal constructor(val filter: Filter) {
        companion object {
            fun createParams(filter: Filter): Params {
                return Params(filter)
            }
        }
    }
}