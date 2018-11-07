package justbucket.videolib.domain.feature.preferences

import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SaveDetailsState @Inject constructor(
        context: CoroutineContext,
        private val preferenceRepository: PreferenceRepository)
    : UseCase<Unit, SaveDetailsState.Params>(context) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        preferenceRepository.saveDetailsSwitchState(params.openDetails)
    }

    data class Params internal constructor(val openDetails: Int) {
        companion object {
            fun createParams(openDetails: Int): Params {
                return Params(openDetails)
            }
        }
    }
}