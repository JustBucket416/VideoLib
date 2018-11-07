package justbucket.videolib.domain.feature.tag

import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GetAllTags @Inject constructor(
        context: CoroutineContext,
        private val tagRepository: TagRepository)
    : UseCase<List<String>, Nothing>(context) {

    override suspend fun run(params: Nothing?) =
            tagRepository.getAllTags()
}