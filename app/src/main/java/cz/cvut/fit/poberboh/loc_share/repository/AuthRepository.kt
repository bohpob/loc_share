package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.AuthApi
import cz.cvut.fit.poberboh.loc_share.network.requests.AuthRequest

/**
 * Repository class for handling authentication-related API calls.
 * @param api The instance of [AuthApi] used for making API calls.
 * @param preferences The instance of [AppPreferences] used for accessing shared preferences.
 */
class AuthRepository(
    private val api: AuthApi,
    private val preferences: AppPreferences
) : BaseRepository() {

    /**
     * Register a new user with the specified username and password.
     */
    suspend fun register(username: String, password: String) = safeApiCall {
        api.register(
            request = AuthRequest(
                username = username,
                password = password
            )
        )
    }

    /**
     * Log in with the specified username and password.
     */
    suspend fun login(username: String, password: String) = safeApiCall {
        val response = api.login(
            request = AuthRequest(
                username = username,
                password = password
            )
        )
        response
    }

    /**
     * Log out the current user.
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        preferences.saveTokens(accessToken, refreshToken)
    }

    /**
     * Log out the current user.
     */
    fun getPasswordMismatchWarning(): String {
        return preferences.getPasswordMismatchWarning()
    }
}