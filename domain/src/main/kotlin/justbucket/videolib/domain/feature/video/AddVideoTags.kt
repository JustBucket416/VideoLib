package justbucket.videolib.domain.feature.video

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class AddVideoTags @Inject constructor(observeScheduler: Scheduler,
                                       private val videoRepository: VideoRepository)
    : CompletableUseCase<AddVideoTags.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return videoRepository.addVideoTags(params.video, params.tags)
    }

    data class Params internal constructor(val video: Video, val tags: List<String>) {
        companion object {
            fun createParams(video: Video, tags: List<String>) =
                    Params(video, tags)
        }
    }
}
