package justbucket.videolib.domain.feature.source

import io.reactivex.Scheduler
import io.reactivex.Single
import justbucket.videolib.domain.repository.SourceRepository
import justbucket.videolib.domain.usecase.SingleUseCase
import javax.inject.Inject

class GetAllSources @Inject constructor(observeScheduler: Scheduler,
                                        private val sourceRepository: SourceRepository)
    : SingleUseCase<List<Int>, Nothing>(observeScheduler) {

    override fun buildUseCase(params: Nothing?): Single<List<Int>> {
        return sourceRepository.getAllSources()
    }
}