package justbucket.videolib.utils

import android.arch.lifecycle.MutableLiveData
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subscribers.DisposableSubscriber
import justbucket.videolib.state.Resource

fun <T> createSingleObserver(succBlock: (T) -> Unit,
                             subBlock: (() -> Unit)? = null,
                             errBlock: ((String) -> Unit)? = null): DisposableSingleObserver<T> {
    return object : DisposableSingleObserver<T>() {
        override fun onSuccess(t: T) {
            succBlock(t)
        }

        override fun onError(e: Throwable) {
            errBlock?.invoke(e.localizedMessage)
        }
    }
}

fun <T, N> createFlowableSubscriber(liveData: MutableLiveData<Resource<N>>, mapper: (T) -> N): DisposableSubscriber<T> {
    return object : DisposableSubscriber<T>() {
        override fun onComplete() {
        }

        override fun onNext(t: T) {
            liveData.postValue(Resource.success(mapper(t)))
        }

        override fun onError(t: Throwable?) {
            liveData.postValue(Resource.error(t?.localizedMessage))
        }
    }
}

fun createCompletableObserver(block: () -> Unit): DisposableCompletableObserver {
    return object : DisposableCompletableObserver() {
        override fun onComplete() {
            block()
        }

        override fun onError(e: Throwable) {

        }
    }
}