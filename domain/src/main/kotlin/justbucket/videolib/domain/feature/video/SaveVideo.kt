package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SaveVideo @Inject constructor(coroutineContext: CoroutineContext,
                                    private val videoRepository: VideoRepository)
    : UseCase<Unit, SaveVideo.Params>(coroutineContext) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        return videoRepository.saveVideo(params.video)
    }

    data class Params internal constructor(val video: Video) {
        companion object {
            fun createParams(video: Video) = Params(video)
        }
    }
}