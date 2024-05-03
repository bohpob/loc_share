package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.AuthRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for the authentication API.
 */
interface AuthApi {
    /**
     * Registers a new user.
     * @param request The registration request.
     */
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Unit

    /**
     * Logs in a user.
     * @param request The login request.
     * @return The access token.
     */
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): TokenResponse
}