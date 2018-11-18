package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddVideo @Inject constructor(
        context: CoroutineContext,
        private val videoRepository: VideoRepository)
    : UseCase<Either<Failure, Boolean>, AddVideo.Params>(context) {

    override suspend fun run(params: Params?): Either<Failure, Boolean> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        return videoRepository.addVideo(params.link, params.tags)
    }

    data class Params internal constructor(val link: String, val tags: List<Tag>) {
        companion object {
            fun createParams(link: String, tags: List<Tag>) =
                    Params(link, tags)
        }
    }
}