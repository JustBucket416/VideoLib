package justbucket.videolib.domain.feature.preferences

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class SaveFilter @Inject constructor(observeScheduler: Scheduler,
                                     private val preferenceRepository: PreferenceRepository)
    : CompletableUseCase<SaveFilter.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return preferenceRepository.saveFilter(params.filter)
    }

    data class Params internal constructor(val filter: Filter) {
        companion object {
            fun createParams(filter: Filter): Params {
                return Params(filter)
            }
        }
    }
}
