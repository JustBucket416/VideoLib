package justbucket.videolib.domain.repository

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
    suspend fun addTag(text: String)

    /**
     * Delete tag from database
     *
     * @param tag - a tag to delete
     */
    suspend fun deleteTag(tag: Tag)

    /**
     * Loads all tags from the db
     *
     * @return - list that contains all tags
     */
    suspend fun getAllTags(): List<Tag>

}