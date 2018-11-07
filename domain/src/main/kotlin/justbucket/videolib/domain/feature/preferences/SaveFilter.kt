package justbucket.videolib.domain.feature.preferences

import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SaveFilter @Inject constructor(
        context: CoroutineContext,
        private val preferenceRepository: PreferenceRepository)
    : UseCase<Unit, SaveFilter.Params>(context) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        preferenceRepository.saveFilter(params.filter)
    }

    data class Params internal constructor(val filter: Filter) {
        companion object {
            fun createParams(filter: Filter): Params {
                return Params(filter)
            }
        }
    }
}