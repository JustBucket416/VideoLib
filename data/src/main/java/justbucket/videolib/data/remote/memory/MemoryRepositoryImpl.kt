package justbucket.videolib.data.remote.memory

import justbucket.videolib.data.db.VideoDatabase
import justbucket.videolib.data.model.LinkEntity
import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.data.model.VideoEntity
import justbucket.videolib.data.remote.MemoryRepository
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

/**
 * A [MemoryRepository] implementation
 */
class MemoryRepositoryImpl @Inject constructor(/*private val requestManager: RequestManager,*/
        database: VideoDatabase) : MemoryRepository {

    private val linkDao = database.linkDao()
    private val videoDao = database.videoDao()
    private val tagDao = database.tagDao()

    private var check = true

    override suspend fun loadFromMemory(path: String, tags: List<String>): Either<Failure, Boolean> {
        runBlocking {
            scanDir(File(path), tags)
        }
        return Either.Right(check)
    }

    private suspend fun scanDir(dir: File, tags: List<String>) {
        val tagId = tagDao.insertTag(TagEntity(text = dir.name))
        dir.listFiles().forEach { file ->
            GlobalScope.launch(Dispatchers.IO) {
                if (!file.isDirectory) {
                    val name = file.nameWithoutExtension

                    /*requestManager.asDrawable()
                            .apply(RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .skipMemoryCache(true))
                            .load(file)
                            .preload()*/

                    val entity = videoDao.getVideoByPath(file.absolutePath)
                    if (entity == null) {
                        val id = videoDao.insertVideo(VideoEntity(sourceId = 0, title = name,
                                videoPath = file.absolutePath, thumbPath = file.absolutePath))
                        linkDao.insertLink(LinkEntity(id, tagId))
                        tags.forEach {
                            linkDao.insertLink(LinkEntity(id, tagDao.getTagId(it)))
                        }
                    } else {
                        check = false
                        tags.forEach {
                            linkDao.insertLink(LinkEntity(entity.id!!, tagDao.getTagId(it)))
                        }
                    }

                } else scanDir(file, tags)
            }
        }
    }
}