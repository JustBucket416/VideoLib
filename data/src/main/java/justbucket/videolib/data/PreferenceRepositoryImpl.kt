package justbucket.videolib.data

import io.reactivex.Completable
import io.reactivex.Single
import justbucket.videolib.data.mapper.FilterMapper
import justbucket.videolib.data.sharedpreferences.PreferencesManager
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
        private val preferencesManager: PreferencesManager,
        private val filterMapper: FilterMapper) : PreferenceRepository {

    override fun saveFilter(filter: Filter): Completable {
        return Completable.defer {
            preferencesManager.saveFilter(filterMapper.mapToData(filter))
            Completable.complete()
        }
    }

    override fun loadFilter(): Single<Filter> {
        return Single.just(filterMapper.mapToDomain(preferencesManager.loadFilter()))
    }

    override fun saveDetailsSwitchState(state: Int): Completable {
        return Completable.defer {
            preferencesManager.saveDetailsSwitchState(state)
            Completable.complete()
        }
    }

    override fun loadDetailsSwitchState(): Single<Int> {
        return Single.just(preferencesManager.loadDetailsSwitchState())
    }
}