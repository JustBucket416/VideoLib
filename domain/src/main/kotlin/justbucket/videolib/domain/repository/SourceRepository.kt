package justbucket.videolib.domain.repository

/**
 * A source repository interface
 */
interface SourceRepository {

    /**
     * load all sources from the db
     *
     * @return list that contains all sources
     */
    fun getAllSources(): List<Int>

}