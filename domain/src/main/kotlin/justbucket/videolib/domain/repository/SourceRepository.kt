package justbucket.videolib.domain.repository

import io.reactivex.Single

/**
 * A source repository interface
 */
interface SourceRepository {

    /**
     * load all sources from the db
     *
     * @return list that contains all sources
     */
    fun getAllSources(): Single<List<Int>>

}