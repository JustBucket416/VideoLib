package justbucket.videolib.domain.feature.preferences

import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class LoadDetailsState @Inject constructor(
        context: CoroutineContext,
        private val preferenceRepository: PreferenceRepository)
    : UseCase<Int, Nothing>(context) {

    override suspend fun run(params: Nothing?): Int {
        return preferenceRepository.loadDetailsSwitchState()
    }
}