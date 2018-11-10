package justbucket.videolib.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import justbucket.videolib.domain.model.Filter

interface PreferenceRepository {

    fun saveFilter(filter: Filter): Completable

    fun loadFilter(): Single<Filter>

    fun saveDetailsSwitchState(state: Int): Completable

    fun loadDetailsSwitchState(): Single<Int>
}