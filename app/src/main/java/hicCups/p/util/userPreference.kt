package hicCups.p.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences

class userPreference(private val context: Context) {

    companion object {

   val Context.dataStore by preferencesDataStore(
            name = "LoginStatus"
        )
        val LOGIN_STATUS_KEY = stringPreferencesKey("LoginStatus")
    }

    val loginStatus: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LOGIN_STATUS_KEY] ?: "loggedOut"

    }

    suspend fun saveLoginStatus(status: String = "loggedOut") {
        context.dataStore.edit { preferences ->
            preferences[LOGIN_STATUS_KEY] = status
        }
    }

}
