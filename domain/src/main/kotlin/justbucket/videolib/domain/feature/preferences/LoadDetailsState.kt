package justbucket.videolib.domain.feature.preferences

import io.reactivex.Scheduler
import io.reactivex.Single
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.SingleUseCase
import javax.inject.Inject

class LoadDetailsState @Inject constructor(observeScheduler: Scheduler,
                                           private val preferenceRepository: PreferenceRepository)
    : SingleUseCase<Int, Nothing>(observeScheduler) {

    override fun buildUseCase(params: Nothing?): Single<Int> {
        return preferenceRepository.loadDetailsSwitchState()
    }
}