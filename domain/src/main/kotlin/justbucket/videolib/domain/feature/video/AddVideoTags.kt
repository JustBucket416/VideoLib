package justbucket.videolib.domain.feature.video

import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddVideoTags @Inject constructor(
        context: CoroutineContext,
        private val videoRepository: VideoRepository)
    : UseCase<Unit, AddVideoTags.Params>(context) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        videoRepository.addVideoTags(params.video, params.tags)
    }

    data class Params internal constructor(val video: Video, val tags: List<Tag>) {
        companion object {
            fun createParams(video: Video, tags: List<Tag>) =
                    Params(video, tags)
        }
    }
}