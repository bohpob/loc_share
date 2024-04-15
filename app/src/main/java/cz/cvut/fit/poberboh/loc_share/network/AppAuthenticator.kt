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

class AppAuthenticator(
    private val api: RefreshApi,
    private val appPreferences: AppPreferences
) : Authenticator, BaseRepository() {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val refreshToken = runBlocking { appPreferences.refreshToken.firstOrNull() }
            return refreshToken?.let {
                runBlocking {
                    when (val tokenResponse = updatedToken()) {
                        is Resource.Success -> {
                            val accessToken = tokenResponse.data.accessToken
                            val newRefreshToken = tokenResponse.data.refreshToken
                            appPreferences.saveTokens(accessToken!!, newRefreshToken!!)
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

    private suspend fun updatedToken(): Resource<TokenResponse> {
        val refreshToken = appPreferences.refreshToken.first()
        return safeApiCall { api.refreshToken(RefreshToken(refreshToken)) }
    }
}