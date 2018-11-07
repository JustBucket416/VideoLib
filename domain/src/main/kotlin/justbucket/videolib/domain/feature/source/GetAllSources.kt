package justbucket.videolib.domain.feature.source

import justbucket.videolib.domain.repository.SourceRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GetAllSources @Inject constructor(
        context: CoroutineContext,
        private val sourceRepository: SourceRepository) :
        UseCase<List<Int>, Nothing>(context) {

    override suspend fun run(params: Nothing?) = sourceRepository.getAllSources()
}