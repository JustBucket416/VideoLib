package justbucket.videolib.domain.usecase

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.schedulers.Schedulers

abstract class SingleUseCase<Type, in Params>(private val observeScheduler: Scheduler) {

    protected val ILLEGAL_ARGUMENT_MESSAGE = "Params can't be null!"

    protected abstract fun buildUseCase(params: Params? = null): Single<Type>

    fun execute(observer: SingleObserver<Type>, params: Params? = null) =
            buildUseCase(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(observer)

}