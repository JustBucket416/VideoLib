package justbucket.videolib.domain.usecase

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

abstract class CompletableUseCase<in Params>(private val observeScheduler: Scheduler) {

    protected val ILLEGAL_ARGUMENT_MESSAGE = "Params can't be null!"

    protected abstract fun buildUseCase(params: Params? = null): Completable

    fun execute(observer: CompletableObserver, params: Params? = null) =
            buildUseCase(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(observer)

}