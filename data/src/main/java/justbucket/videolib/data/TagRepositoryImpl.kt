package justbucket.videolib.data

import io.reactivex.Completable
import io.reactivex.Single
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(videoDatabase: VideoDatabase) : TagRepository {

    private val tagDao = videoDatabase.tagDao()

    override fun addTag(text: String): Completable {
        return Completable.defer {
            tagDao.insertTag(TagEntity(text = text))
            Completable.complete()
        }
    }

    override fun deleteTag(tag: String): Completable {
        return Completable.defer {
            tagDao.deleteTag(tagDao.findTagByText(tag))
            Completable.complete()
        }
    }

    override fun getAllTags(): Single<List<String>> {
        return tagDao.getAllTags().map { it.map { it.text } }
    }
}