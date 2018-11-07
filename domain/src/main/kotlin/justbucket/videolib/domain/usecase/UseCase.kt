package justbucket.videolib.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 *
 * By convention each [UseCase] implementation will execute its job in a background thread
 * (kotlin coroutine) and will post the result in the UI thread.
 */

abstract class UseCase<out Type, in Params>(private val context: CoroutineContext) where Type : Any {

    //not yet supported
    /*companion object {
        protected const val ILLEGAL_EXCEPTION_MESSAGE = "Params can't be null"
    }*/

    protected val ILLEGAL_EXCEPTION_MESSAGE = "Params can't be null"

    protected abstract suspend fun run(params: Params? = null): Type

    fun execute(onResult: ((Type) -> Unit)? = null, params: Params? = null) {
        val job = GlobalScope.async(context = Dispatchers.IO) { run(params) }
        GlobalScope.launch(context = context) { onResult?.invoke(job.await()) }
    }
}
