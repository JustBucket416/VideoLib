package justbucket.videolib.domain.feature.tag

import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DeleteTag @Inject constructor(
        context: CoroutineContext,
        private val tagRepository: TagRepository)
    : UseCase<Unit, DeleteTag.Params>(context) {

    override suspend fun run(params: Params?) {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        tagRepository.deleteTag(params.tag)
    }

    data class Params internal constructor(val tag: String) {
        companion object {
            fun createParams(tag: String) =
                    Params(tag)
        }
    }
}