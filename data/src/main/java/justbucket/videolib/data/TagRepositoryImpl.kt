package justbucket.videolib.data

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.mapper.mapToData
import justbucket.videolib.data.mapper.mapToDomain
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.domain.model.Tag
import justbucket.videolib.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(videoDatabase: VideoDatabase) : TagRepository {

    private val tagDao = videoDatabase.tagDao()

    override suspend fun addTag(text: String) {
        tagDao.insertTag(TagEntity(text = text))
    }

    override suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag.mapToData())
    }

    override suspend fun getAllTags(): List<Tag> {
        return tagDao.getAllTags().map { it.mapToDomain() }
    }
}