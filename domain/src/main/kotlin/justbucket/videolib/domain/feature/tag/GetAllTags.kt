package justbucket.videolib.domain.feature.tag

import io.reactivex.Scheduler
import io.reactivex.Single
import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.usecase.SingleUseCase
import javax.inject.Inject

class GetAllTags @Inject constructor(observeScheduler: Scheduler,
                                     private val tagRepository: TagRepository)
    : SingleUseCase<List<Tag>, Nothing>(observeScheduler) {

    override fun buildUseCase(params: Nothing?): Single<List<Tag>> {
        return tagRepository.getAllTags()
    }
}