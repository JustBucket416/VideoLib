package justbucket.videolib

import android.app.Activity
import android.app.Application
import android.app.Service
import android.support.v4.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import justbucket.videolib.di.DaggerAppComponent
import javax.inject.Inject

/**
 * An [Application] subclass which implements activity and fragment injections
 */
class VideoApplication : Application(), HasActivityInjector, HasSupportFragmentInjector,
        HasServiceInjector {

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
    }
}