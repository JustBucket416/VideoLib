package justbucket.videolib.data.remote

import justbucket.videolib.data.model.TagEntity
import justbucket.videolib.domain.exception.Failure
import justbucket.videolib.domain.functional.Either

/**
 * A repository interface which loads videos from local memory
 */
interface MemoryRepository {

    /**
     * loads video from memory
     *
     * @param path - canonical file path
     *
     * @return a video entity
     */
    suspend fun loadFromMemory(path: String, tags: List<TagEntity>): Either<Failure, Boolean>
}