package justbucket.videolib.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import justbucket.videolib.data.PreferenceRepositoryImpl
import justbucket.videolib.data.SourceRepositoryImpl
import justbucket.videolib.data.TagRepositoryImpl
import justbucket.videolib.data.VideoRepositoryImpl
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.FilterMapper
import justbucket.videolib.data.mapper.VideoMapper
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.data.remote.YoutubeRepository
import justbucket.videolib.data.remote.memory.MemoryRepositoryImpl
import justbucket.videolib.data.remote.youtube.RetrofitHelper
import justbucket.videolib.data.remote.youtube.YoutubeRepositoryImpl
import justbucket.videolib.data.sharedpreferences.PreferencesManager
import justbucket.videolib.domain.repository.PreferenceRepository
import justbucket.videolib.domain.repository.SourceRepository
import justbucket.videolib.domain.repository.TagRepository
import justbucket.videolib.domain.repository.VideoRepository

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
    fun provideYOutubeRepository(retrofitHelper: RetrofitHelper,
                                 database: VideoDatabase): YoutubeRepository {
        return YoutubeRepositoryImpl(retrofitHelper, database)
    }

    @Provides
    fun provideVideoRepository(videoDatabase: VideoDatabase, videoMapper: VideoMapper,
                               memoryRepository: MemoryRepository,
                               youtubeRepository: YoutubeRepository): VideoRepository {
        return VideoRepositoryImpl(videoDatabase, videoMapper, memoryRepository, youtubeRepository)
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
    fun providePreferencesRepository(preferencesManager: PreferencesManager,
                                     filterMapper: FilterMapper): PreferenceRepository {
        return PreferenceRepositoryImpl(preferencesManager, filterMapper)
    }
}