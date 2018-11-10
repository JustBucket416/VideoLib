package justbucket.videolib.di.module

import android.app.Application
import android.content.Context
import com.bumptech.glide.Glide
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
abstract class AppModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun provideGlideManager(context: Context) = Glide.with(context)

        @Provides
        @JvmStatic
        fun provideObserveSheduler() = AndroidSchedulers.mainThread()
    }

    @Binds
    abstract fun bindAppContext(application: Application): Context

}