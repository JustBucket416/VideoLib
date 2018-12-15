package justbucket.videolib.domain.feature.ddsearch

import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import justbucket.videolib.domain.repository.CategoryRepository
import justbucket.videolib.domain.usecase.UseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchByImage @Inject constructor(coroutineContext: CoroutineContext,
                                        private val categoryRepository: CategoryRepository) : UseCase<Either<Failure, ArrayList<String>>, SearchByImage.Params>(coroutineContext) {

    override suspend fun run(params: Params?): Either<Failure, ArrayList<String>> {
        if (params == null) throw IllegalArgumentException(ILLEGAL_EXCEPTION_MESSAGE)
        return categoryRepository.getTags(params.base64)
    }

    data class Params internal constructor(val base64: String) {
        companion object {
            @JvmStatic
            fun createParams(base64: String) = Params(base64)
        }
    }
}