package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.network.requests.GPSIncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest

class BasicRepository(
    private val api: BasicApi,
    private val preferences: AppPreferences
) : BaseRepository() {

    suspend fun getUsername() = safeApiCall {
        api.getUsername()
    }

    suspend fun createIncident(incident: IncidentRequest) = safeApiCall {
        api.createIncident(incident)
    }

    suspend fun createGPSIncident(id: String, gpsIncident: GPSIncidentRequest) = safeApiCall {
        api.createGPSIncident(id, gpsIncident)
    }

    suspend fun stop(id: String) = safeApiCall {
        api.stop(id)
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