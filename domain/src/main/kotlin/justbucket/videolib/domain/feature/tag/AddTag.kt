package justbucket.videolib.domain.feature.tag

import io.reactivex.Completable
import io.reactivex.Scheduler
import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.CompletableUseCase
import javax.inject.Inject

class AddTag @Inject constructor(observeScheduler: Scheduler,
                                 private val tagRepository: TagRepository)
    : CompletableUseCase<AddTag.Params>(observeScheduler) {

    override fun buildUseCase(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException(ILLEGAL_ARGUMENT_MESSAGE)
        return tagRepository.addTag(params.text)
    }

    data class Params internal constructor(val text: String) {
        companion object {
            fun createParams(text: String) =
                    Params(text)
        }
    }
}