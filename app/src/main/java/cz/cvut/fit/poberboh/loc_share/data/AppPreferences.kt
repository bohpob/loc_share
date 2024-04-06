package cz.cvut.fit.poberboh.loc_share.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.cvut.fit.poberboh.loc_share.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

class AppPreferences(context: Context) {

    private val appContext = context.applicationContext
    private val appResources = context.resources

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    suspend fun saveAccessToken(accessToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun deleteAccessToken() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getCategoriesFromResources(): List<String> {
        return appResources.getStringArray(R.array.categories).toList()
    }

    fun getRedButtonProperties(): Triple<Boolean, Int, String> {
        return Triple(
            false,
            appContext.getColor(R.color.red),
            appResources.getString(R.string.text_button_deactivation)
        )
    }

    fun getGreenButtonProperties(): Triple<Boolean, Int, String> {
        return Triple(
            true,
            appContext.getColor(R.color.green),
            appResources.getString(R.string.text_button_activation)
        )
    }

    fun getUsernameForm(): String {
        return appResources.getString(R.string.username)
    }

    fun getPasswordMismatchWarning(): String {
        return appResources.getString(R.string.password_mismatch_warning)
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("jwt")
    }
}