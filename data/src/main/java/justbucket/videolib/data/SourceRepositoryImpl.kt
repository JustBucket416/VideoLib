package justbucket.videolib.data

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.domain.repository.SourceRepository
import javax.inject.Inject

class SourceRepositoryImpl @Inject constructor(videoDatabase: VideoDatabase) : SourceRepository {

    private val sourceDao = videoDatabase.sourceDao()

    override fun getAllSources(): List<Int> {
        return sourceDao.getAllSources().map { it.source }
    }
}