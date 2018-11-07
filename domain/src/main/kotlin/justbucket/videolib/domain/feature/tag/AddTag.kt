package justbucket.videolib.domain.feature.tag

import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddTag @Inject constructor(
        context: CoroutineContext,
        private val tagRepository: TagRepository)
    : UseCase<Unit, AddTag.Params>(context) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        tagRepository.addTag(params.text)
    }

    data class Params internal constructor(val text: String) {
        companion object {
            fun createParams(text: String) =
                    Params(text)
        }
    }
}