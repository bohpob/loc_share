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

// DataStore is a new data storage solution that stores key-value pairs in a file on the device.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

/**
 * Class for managing the app preferences.
 * @property context The context of the app.
 */
class AppPreferences(context: Context) {
    // Keys for the access token and the refresh token.
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    }

    // The context of the app.
    private val appContext = context.applicationContext

    // The resources of the app.
    private val appResources = context.resources

    // The access token.
    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    // The refresh token.
    val refreshToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }

    /**
     * Saves the access token and the refresh token to the data store.
     * @param accessToken The access token.
     * @param refreshToken The refresh token.
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    /**
     * Deletes the access token and the refresh token from the data store.
     */
    suspend fun deleteAccessToken() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Gets the categories from the resources.
     * @return The list of categories.
     */
    fun getCategoriesFromResources(): List<String> {
        return appResources.getStringArray(R.array.categories).toList()
    }

    /**
     * Gets the categories from the resources.
     * @return The list of categories.
     */
    fun getRedButtonProperties(): Triple<Boolean, Int, String> {
        return Triple(
            false,
            appContext.getColor(R.color.red),
            appResources.getString(R.string.text_button_deactivation)
        )
    }

    /**
     * Gets the categories from the resources.
     * @return The list of categories.
     */
    fun getGreenButtonProperties(): Triple<Boolean, Int, String> {
        return Triple(
            true,
            appContext.getColor(R.color.green),
            appResources.getString(R.string.text_button_activation)
        )
    }

    /**
     * Gets the username form from the resources.
     * @return The username form.
     */
    fun getUsernameForm(): String {
        return appResources.getString(R.string.username)
    }

    /**
     * Gets the password form from the resources.
     * @return The password form.
     */
    fun getPasswordMismatchWarning(): String {
        return appResources.getString(R.string.password_mismatch_warning)
    }
}