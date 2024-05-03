package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.RefreshToken
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for the refresh token API.
 */
interface RefreshApi {
    /**
     * Refreshes the access token.
     * @param refreshToken The refresh token.
     * @return The new access token.
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): TokenResponse
}