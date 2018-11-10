package justbucket.videolib.data.remote

import io.reactivex.Single

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
    fun loadFromMemory(path: String, tags: List<String>): Single<Boolean>
}