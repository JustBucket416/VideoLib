package justbucket.videolib.data

import justbucket.videolib.data.mapper.FilterMapper
import justbucket.videolib.data.sharedpreferences.PreferencesManager
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
        private val preferencesManager: PreferencesManager,
        private val filterMapper: FilterMapper) : PreferenceRepository {

    override suspend fun saveFilter(filter: Filter) {
        preferencesManager.saveFilter(filterMapper.mapToData(filter))
    }

    override suspend fun loadFilter(): Filter {
        return filterMapper.mapToDomain(preferencesManager.loadFilter())
    }

    override suspend fun saveDetailsSwitchState(state: Int) {
        preferencesManager.saveDetailsSwitchState(state)
    }

    override suspend fun loadDetailsSwitchState(): Int {
        return preferencesManager.loadDetailsSwitchState()
    }
}