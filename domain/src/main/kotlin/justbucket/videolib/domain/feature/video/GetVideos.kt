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
        return videoRepository.getFilteredVideos(params.filter)
    }

    data class Params internal constructor(val filter: Filter) {
        companion object {
            fun createParams(filter: Filter): Params {
                return Params(filter)
            }
        }
    }
}