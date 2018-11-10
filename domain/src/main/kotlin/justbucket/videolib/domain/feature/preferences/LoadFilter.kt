package justbucket.videolib.domain.feature.preferences

import io.reactivex.Scheduler
import io.reactivex.Single
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.SingleUseCase
import javax.inject.Inject

/*
class LoadFilter @Inject constructor(
        context: CoroutineContext,
        private val preferenceRepository: PreferenceRepository)
    : UseCase<Filter, Nothing>(context) {

    override suspend fun run(params: Nothing?): Filter {
        return preferenceRepository.loadFilter()
    }
}*/

class LoadFilter @Inject constructor(observeScheduler: Scheduler,
                                     private val preferenceRepository: PreferenceRepository)
    : SingleUseCase<Filter, Nothing>(observeScheduler) {

    override fun buildUseCase(params: Nothing?): Single<Filter> {
        return preferenceRepository.loadFilter()
    }
}
