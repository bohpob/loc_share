package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.AuthApi
import cz.cvut.fit.poberboh.loc_share.network.requests.AuthRequest

class AuthRepository(
    private val api: AuthApi,
    private val preferences: AppPreferences
) : BaseRepository() {

    suspend fun register(username: String, password: String) = safeApiCall {
        api.register(
            request = AuthRequest(
                username = username,
                password = password
            )
        )
    }

    suspend fun login(username: String, password: String) = safeApiCall {
        val response = api.login(
            request = AuthRequest(
                username = username,
                password = password
            )
        )
        response
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        preferences.saveTokens(accessToken, refreshToken)
    }

    fun getPasswordMismatchWarning(): String {
        return preferences.getPasswordMismatchWarning()
    }
}