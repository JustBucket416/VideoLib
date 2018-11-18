package justbucket.videolib.data

import justbucket.videolib.data.mapper.mapToData
import justbucket.videolib.data.mapper.mapToDomain
import justbucket.videolib.data.sharedpreferences.PreferencesManager
import justbucket.videolib.domain.model.Filter
import justbucket.videolib.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
        private val preferencesManager: PreferencesManager) : PreferenceRepository {

    override suspend fun saveFilter(filter: Filter) {
        preferencesManager.saveFilter(filter.mapToData())
    }

    override suspend fun loadFilter(): Filter {
        return preferencesManager.loadFilter().mapToDomain()
    }

    override suspend fun saveDetailsSwitchState(state: Int) {
        preferencesManager.saveDetailsSwitchState(state)
    }

    override suspend fun loadDetailsSwitchState(): Int {
        return preferencesManager.loadDetailsSwitchState()
    }
}