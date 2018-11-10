package justbucket.videolib.data.sharedpreferences

import android.content.Context
import com.google.gson.Gson
import justbucket.videolib.data.model.FilterEntity

class PreferencesManager private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "prefs"
        private const val FILTER_KEY = "filter-key"
        private const val DETAILS_SWITCH_STATE_KEY = "details-switch-state-key"
        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            if (INSTANCE == null) {
                synchronized(PreferencesManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = PreferencesManager(context)
                    }
                    return INSTANCE as PreferencesManager
                }
            }
            return INSTANCE as PreferencesManager
        }
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveFilter(entity: FilterEntity) {
        preferences.edit().putString(FILTER_KEY, filterToJson(entity)).apply()
    }

    fun loadFilter(): FilterEntity {
        val filterString = preferences.getString(FILTER_KEY, null)
        return if (filterString != null) jsonToFilter(filterString)
        else FilterEntity()
    }

    fun saveDetailsSwitchState(state: Int) {
        preferences.edit().putInt(DETAILS_SWITCH_STATE_KEY, state).apply()
    }

    fun loadDetailsSwitchState(): Int {
        return preferences.getInt(DETAILS_SWITCH_STATE_KEY, 0)
    }

    private fun filterToJson(filterTemplate: FilterEntity): String {
        return Gson().toJson(filterTemplate)
    }

    private fun jsonToFilter(json: String): FilterEntity {
        return Gson().fromJson(json, FilterEntity::class.java)
    }


}