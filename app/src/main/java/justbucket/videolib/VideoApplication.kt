package justbucket.videolib

import android.app.Activity
import android.app.Application
import android.app.Service
import android.support.v4.app.Fragment
import android.util.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import justbucket.videolib.di.DaggerAppComponent
import java.io.IOException
import java.net.SocketException
import javax.inject.Inject

/**
 * An [Application] subclass which implements activity and fragment injections
 */
class VideoApplication : Application(), HasActivityInjector, HasSupportFragmentInjector,
        HasServiceInjector {

    companion object {
        private const val TAG = "VideoLib"
    }

    @Inject
    lateinit var androidActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var androidFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var androidServiceInjector: DispatchingAndroidInjector<Service>

    override fun activityInjector(): AndroidInjector<Activity> {
        return androidActivityInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return androidFragmentInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return androidServiceInjector
    }

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        RxJavaPlugins.setErrorHandler {
            val throwable = if (it is UndeliverableException) it.cause else it
            when {
                (throwable is IOException || throwable is SocketException) -> {
                    return@setErrorHandler
                }
                throwable is InterruptedException -> return@setErrorHandler
                (throwable is NullPointerException || throwable is IllegalArgumentException) -> {
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable)
                    return@setErrorHandler
                }
                throwable is IllegalStateException -> {
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable)
                    return@setErrorHandler
                }
                else -> Log.w(TAG, throwable)
            }
        }
    }
}