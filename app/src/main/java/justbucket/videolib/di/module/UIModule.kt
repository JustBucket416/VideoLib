package justbucket.videolib.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import justbucket.videolib.ImportActivity
import justbucket.videolib.SecondActivity
import justbucket.videolib.VideoActivity
import justbucket.videolib.fragment.FilterFragment
import justbucket.videolib.fragment.GridFragment
import justbucket.videolib.fragment.ImagePagerFragment
import justbucket.videolib.fragment.SelectTagsFragment
import justbucket.videolib.service.ImportService
import justbucket.videolib.service.MediaPlayerService

@Module
abstract class UIModule {

    @ContributesAndroidInjector
    abstract fun contributeImportActivity(): ImportActivity

    @ContributesAndroidInjector
    abstract fun contributeGridFragment(): GridFragment

    @ContributesAndroidInjector
    abstract fun contributeFilterFragment(): FilterFragment

    @ContributesAndroidInjector
    abstract fun contributeImagePagerFragment(): ImagePagerFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectTagsFragment(): SelectTagsFragment

    @ContributesAndroidInjector
    abstract fun contributeVideoActivity(): VideoActivity

    @ContributesAndroidInjector
    abstract fun contributeSecondInjector(): SecondActivity

    @ContributesAndroidInjector
    abstract fun contributeImportService(): ImportService

    @ContributesAndroidInjector
    abstract fun contributeMediaPlayerService(): MediaPlayerService
}