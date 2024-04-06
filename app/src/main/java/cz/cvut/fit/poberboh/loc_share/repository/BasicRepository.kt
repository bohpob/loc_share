package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.LocationRequest

class BasicRepository(
    private val api: BasicApi,
    private val preferences: AppPreferences
) : BaseRepository() {

    suspend fun getUsername() = safeApiCall {
        api.getUsername()
    }

    suspend fun createIncident(category: String, text: String?) = safeApiCall {
        api.createIncident(
            request = IncidentRequest(
                category = category,
                note = text
            )
        )
    }

    suspend fun recordLocation(incidentId: Long, latitude: String, longitude: String) =
        safeApiCall {
            api.recordLocation(
                request = LocationRequest(
                    incidentId = incidentId,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }

    suspend fun stopShare(id: Long) = safeApiCall {
        api.stopShare(id = id)
    }

    fun getCategoriesFromResources(): List<String> {
        return preferences.getCategoriesFromResources()
    }

    fun getRedButtonProperties(): Triple<Boolean, Int, String> {
        return preferences.getRedButtonProperties()
    }

    fun getGreenButtonProperties(): Triple<Boolean, Int, String> {
        return preferences.getGreenButtonProperties()
    }

    fun getUsernameForm(): String {
        return preferences.getUsernameForm()
    }
}