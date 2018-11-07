package justbucket.videolib.domain.feature.preferences

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LoadFilter @Inject constructor(
        context: CoroutineContext,
        private val preferenceRepository: PreferenceRepository)
    : UseCase<Filter, Nothing>(context) {

    override suspend fun run(params: Nothing?): Filter {
        return preferenceRepository.loadFilter()
    }
}