package justbucket.videolib.data.remote.memory

import io.reactivex.Single
import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.data.remote.MemoryRepository
import java.io.File
import javax.inject.Inject

/**
 * A [MemoryRepository] implementation
 */
class MemoryRepositoryImpl @Inject constructor(database: VideoDatabase) : MemoryRepository {

    private val linkDao = database.linkDao()
    private val videoDao = database.videoDao()
    private val tagDao = database.tagDao()

    override fun loadFromMemory(path: String, tags: List<String>): Single<Boolean> {
        return Single.defer {
            Single.just(scanDir(File(path), tags, true))
        }
    }

    private fun scanDir(dir: File, tags: List<String>, check: Boolean): Boolean {
        val tagId = tagDao.insertTag(TagEntity(text = dir.name))
        var innerCheck = check
        dir.listFiles().forEach { file ->
            innerCheck = if (!file.isDirectory) {
                val name = file.nameWithoutExtension

                val entity = videoDao.getVideoByPath(file.absolutePath)
                if (entity == null) {
                    val id = videoDao.insertVideo(VideoEntity(sourceId = 0, title = name,
                            videoPath = file.absolutePath, thumbPath = file.absolutePath))
                    linkDao.insertLink(LinkEntity(id, tagId))
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(id, tagDao.getTagId(it)))
                    }
                    true
                } else {
                    tags.forEach {
                        linkDao.insertLink(LinkEntity(entity.id!!, tagDao.getTagId(it)))
                    }
                    false
                }
            } else scanDir(file, tags, innerCheck)
        }
        return innerCheck
    }
}