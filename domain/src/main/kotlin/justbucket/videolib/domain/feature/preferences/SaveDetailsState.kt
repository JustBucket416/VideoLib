package justbucket.videolib.domain.feature.preferences

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class SaveDetailsState @Inject constructor(observeScheduler: Scheduler,
                                           private val preferenceRepository: PreferenceRepository)
    : CompletableUseCase<SaveDetailsState.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return preferenceRepository.saveDetailsSwitchState(params.openDetails)
    }

    data class Params internal constructor(val openDetails: Int) {
        companion object {
            fun createParams(openDetails: Int): Params {
                return Params(openDetails)
            }
        }
    }
}
