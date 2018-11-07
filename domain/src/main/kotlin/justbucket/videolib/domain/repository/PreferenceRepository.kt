package justbucket.videolib.domain.repository

import justbucket.videolib.domain.model.Filter

interface PreferenceRepository {

    suspend fun saveFilter(filter: Filter)

    suspend fun loadFilter(): Filter

    suspend fun saveDetailsSwitchState(state: Int)

    suspend fun loadDetailsSwitchState(): Int
}