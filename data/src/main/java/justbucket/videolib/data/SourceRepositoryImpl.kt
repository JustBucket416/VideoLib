package justbucket.videolib.data

import io.reactivex.Single
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.domain.repository.SourceRepository
import javax.inject.Inject

class SourceRepositoryImpl @Inject constructor(videoDatabase: VideoDatabase) : SourceRepository {

    private val sourceDao = videoDatabase.sourceDao()

    override fun getAllSources(): Single<List<Int>> {
        return sourceDao.getAllSources().map { list -> list.map { it.source } }
    }
}