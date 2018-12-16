package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GetTempVideos @Inject constructor(coroutineContext: CoroutineContext,
                                        private val videoRepository: VideoRepository)
    : UseCase<Either<Failure, List<Video>>, GetTempVideos.Params>(coroutineContext) {

    override suspend fun run(params: Params?): Either<Failure, List<Video>> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        return videoRepository.loadVideosByTempTag(params.tag)
    }

    data class Params internal constructor(val tag: String) {
        companion object {
            fun createParams(tag: String) = Params(tag)
        }
    }
}