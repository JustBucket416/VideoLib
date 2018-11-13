package justbucket.videolib.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import justbucket.videolib.domain.model.Tag

/**
 * A tag repository interface
 */
interface TagRepository {

    /**
     * Add tag to the database
     *
     * @param text - tag text
     */
    fun addTag(text: String): Completable

    /**
     * Delete tag from database
     *
     * @param tag - a tag to delete
     */
    fun deleteTag(tag: Tag): Completable

    /**
     * Loads all tags from the db
     *
     * @return - list that contains all tags
     */
    fun getAllTags(): Single<List<Tag>>

}