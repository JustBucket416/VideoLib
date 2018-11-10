package justbucket.videolib.domain.feature.video

import io.reactivex.Flowable
import io.reactivex.Scheduler
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.model.Video
import justbucket.videolib.domain.repository.VideoRepository
import justbucket.videolib.domain.usecase.FlowableUseCase
import javax.inject.Inject

class GetVideos @Inject constructor(observeScheduler: Scheduler,
                                    private val videoRepository: VideoRepository)
    : FlowableUseCase<List<Video>, GetVideos.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Flowable<List<Video>> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
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
