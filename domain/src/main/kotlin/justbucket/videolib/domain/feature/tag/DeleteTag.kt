package justbucket.videolib.domain.feature.tag

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class DeleteTag @Inject constructor(observeScheduler: Scheduler,
                                    private val tagRepository: TagRepository)
    : CompletableUseCase<DeleteTag.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return tagRepository.deleteTag(params.tag)
    }

    data class Params internal constructor(val tag: String) {
        companion object {
            fun createParams(tag: String) =
                    Params(tag)
        }
    }
}
