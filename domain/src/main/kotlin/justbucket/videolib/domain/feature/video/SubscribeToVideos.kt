package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SubscribeToVideos @Inject constructor(private val coroutineContext: CoroutineContext,
                                            private val videoRepository: VideoRepository)
    : UseCase<Unit, SubscribeToVideos.Params>(coroutineContext) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        videoRepository.subscribeToVideos(params.onNext, params.filter, coroutineContext)
    }

    data class Params internal constructor(val onNext: (List<Video>) -> Unit, val filter: Filter) {
        companion object {
            fun createParams(onNext: (List<Video>) -> Unit, filter: Filter): Params {
                return Params(onNext, filter)
            }
        }
    }
}