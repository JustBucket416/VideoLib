package justbucket.videolib.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import justbucket.videolib.data.*
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.remote.ImageRepository
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.RetrofitHelper
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.data.remote.deep_detect.DDApi
import justbucket.videolib.data.remote.deep_detect.DDConstants
import justbucket.videolib.data.remote.deep_detect.ImageRepositoryImpl
import justbucket.videolib.data.remote.memory.MemoryRepositoryImpl
import justbucket.videolib.data.remote.youtube.YoutubeAPI
import justbucket.videolib.data.remote.youtube.YoutubeConstants
import justbucket.videolib.data.remote.youtube.YoutubeRepositoryImpl
import justbucket.videolib.data.sharedpreferences.PreferencesManager
import justbucket.videolib.domain.repository.*

@Module
class DataModule {

    @Provides
    fun providePreferencesManager(context: Context) = PreferencesManager.getInstance(context)

    @Provides
    fun provideVideoDatabase(context: Context) = VideoDatabase.getInstance(context)

    @Provides
    fun provideMemoryRepository(database: VideoDatabase): MemoryRepository {
        return MemoryRepositoryImpl(database)
    }

    @Provides
    fun provideYoutubeAPI(retrofitHelper: RetrofitHelper): YoutubeAPI {
        return retrofitHelper.buildApi(YoutubeConstants.BASE_URL)
    }

    @Provides
    fun provideDDApi(retrofitHelper: RetrofitHelper): DDApi {
        return retrofitHelper.buildApi(DDConstants.CLASSIFICATION_ENDPOINT)
    }

    @Provides
    fun provideYoutubeRepository(youtubeAPI: YoutubeAPI,
                                 database: VideoDatabase): YoutubeRepository {
        return YoutubeRepositoryImpl(youtubeAPI, database)
    }

    @Provides
    fun provideImageRepository(ddApi: DDApi): ImageRepository {
        return ImageRepositoryImpl(ddApi)
    }

    @Provides
    fun provideVideoRepository(videoDatabase: VideoDatabase, memoryRepository: MemoryRepository,
                               youtubeRepository: YoutubeRepository): VideoRepository {
        return VideoRepositoryImpl(videoDatabase, memoryRepository, youtubeRepository)
    }

    @Provides
    fun provideTagRepository(videoDatabase: VideoDatabase): TagRepository {
        return TagRepositoryImpl(videoDatabase)
    }

    @Provides
    fun provideSourceRepository(videoDatabase: VideoDatabase): SourceRepository {
        return SourceRepositoryImpl(videoDatabase)
    }

    @Provides
    fun provideCategoryrepository(imageRepository: ImageRepository): CategoryRepository {
        return CategoryRepositoryImpl(imageRepository)
    }

    @Provides
    fun providePreferencesRepository(preferencesManager: PreferencesManager): PreferenceRepository {
        return PreferenceRepositoryImpl(preferencesManager)
    }
}