package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.RecordLocationRequest

/**
 * Repository class for handling basic API calls.
 * @param api The instance of [BasicApi] used for making API calls.
 * @param preferences The instance of [AppPreferences] used for accessing shared preferences.
 */
class BasicRepository(
    private val api: BasicApi,
    private val preferences: AppPreferences
) : BaseRepository() {

    /**
     * Get the username of the current user.
     */
    suspend fun getUsername() = safeApiCall {
        api.getUsername()
    }

    /**
     * Create an incident with the specified category and text.
     */
    suspend fun createIncident(category: String, text: String?) = safeApiCall {
        api.createIncident(
            request = IncidentRequest(
                category = category,
                note = text ?: ""
            )
        )
    }

    /**
     * Record the location of the incident.
     */
    suspend fun recordLocation(incidentId: Long, latitude: Double, longitude: Double) =
        safeApiCall {
            api.recordLocation(
                request = RecordLocationRequest(
                    incidentId = incidentId,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }

    /**
     * Start sharing the location with the specified user.
     */
    suspend fun stopShare(id: Long) = safeApiCall {
        api.stopShare(id = id)
    }

    /**
     * Get the categories from the preferences.
     */
    fun getCategoriesFromResources(): List<String> {
        return preferences.getCategoriesFromResources()
    }

    /**
     * Get the red button properties from the preferences.
     */
    fun getRedButtonProperties(): Triple<Boolean, Int, String> {
        return preferences.getRedButtonProperties()
    }

    /**
     * Get the green button properties from the preferences.
     */
    fun getGreenButtonProperties(): Triple<Boolean, Int, String> {
        return preferences.getGreenButtonProperties()
    }

    /**
     * Get the username form the preferences.
     */
    fun getUsernameForm(): String {
        return preferences.getUsernameForm()
    }
}