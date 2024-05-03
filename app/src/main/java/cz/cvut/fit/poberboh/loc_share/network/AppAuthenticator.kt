package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.RefreshApi
import cz.cvut.fit.poberboh.loc_share.network.requests.RefreshToken
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import cz.cvut.fit.poberboh.loc_share.repository.BaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Authenticator class for handling token refresh.
 * @param api The instance of [RefreshApi] used for making API calls.
 * @param appPreferences The instance of [AppPreferences] used for accessing shared preferences.
 */
class AppAuthenticator(
    private val api: RefreshApi,
    private val appPreferences: AppPreferences
) : Authenticator, BaseRepository() {

    /**
     * Authenticates the request by refreshing the access token.
     * @param route The route of the request.
     * @param response The response from the server.
     * @return The new request with the updated access token.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // Check if the response code is 401
        if (response.code == 401) {
            // Get the refresh token from the app preferences
            val refreshToken = runBlocking { appPreferences.refreshToken.firstOrNull() }
            // Refresh the access token
            return refreshToken?.let {
                runBlocking {
                    // Get the updated access token
                    when (val tokenResponse = updatedToken()) {
                        is Resource.Success -> { // If the token was successfully updated
                            val accessToken = tokenResponse.data.accessToken
                            val newRefreshToken = tokenResponse.data.refreshToken
                            // Save the new tokens to the shared preferences
                            appPreferences.saveTokens(accessToken!!, newRefreshToken!!)
                            // Return the new request with the updated access token
                            response.request.newBuilder()
                                .header("Authorization", "Bearer $accessToken")
                                .build()
                        }

                        else -> null
                    }
                }
            }
        }
        return null
    }

    /**
     * Refreshes the access token using the refresh token.
     * @return The updated access token.
     */
    private suspend fun updatedToken(): Resource<TokenResponse> {
        val refreshToken = appPreferences.refreshToken.first()
        return safeApiCall { api.refreshToken(RefreshToken(refreshToken)) }
    }
}