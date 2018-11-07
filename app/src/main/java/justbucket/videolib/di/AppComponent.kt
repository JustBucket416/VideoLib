package justbucket.videolib.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import justbucket.videolib.VideoApplication
import justbucket.videolib.di.module.AppModule
import justbucket.videolib.di.module.DataModule
import justbucket.videolib.di.module.UIModule
import justbucket.videolib.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    AppModule::class,
    UIModule::class,
    ViewModelModule::class,
    DataModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: VideoApplication)
}