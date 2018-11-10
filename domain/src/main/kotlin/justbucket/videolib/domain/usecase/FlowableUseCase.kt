package justbucket.videolib.domain.usecase

import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

abstract class FlowableUseCase<Type, in Params>(private val observeScheduler: Scheduler) {

    protected val ILLEGAL_ARGUMENT_MESSAGE = "Params can't be null!"

    protected abstract fun buildUseCase(params: Params? = null): Flowable<Type>

    fun execute(subscriber: FlowableSubscriber<Type>, params: Params? = null) =
            buildUseCase(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(subscriber)
}