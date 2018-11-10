package justbucket.videolib.domain.feature.video

import io.reactivex.Scheduler
import io.reactivex.Single
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.SingleUseCase
import javax.inject.Inject

class AddVideo @Inject constructor(obserceScheduler: Scheduler,
                                   private val videoRepository: VideoRepository)
    : SingleUseCase<Boolean, AddVideo.Params>(obserceScheduler) {

    override fun buildUseCase(params: Params?): Single<Boolean> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return videoRepository.addVideo(params.link, params.tags)
    }

    data class Params internal constructor(val link: String, val tags: List<String>) {
        companion object {
            fun createParams(link: String, tags: List<String>) =
                    Params(link, tags)
        }
    }
}
