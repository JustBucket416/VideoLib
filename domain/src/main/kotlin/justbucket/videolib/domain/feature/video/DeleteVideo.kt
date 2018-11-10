package justbucket.videolib.domain.feature.video

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class DeleteVideo @Inject constructor(observeScheduler: Scheduler,
                                      private val videoRepository: VideoRepository)
    : CompletableUseCase<DeleteVideo.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return videoRepository.deleteVideo(params.video)
    }

    data class Params internal constructor(val video: Video) {
        companion object {
            fun createParams(video: Video) =
                    Params(video)
        }
    }
}
