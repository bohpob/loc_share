package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.AuthRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Unit

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): TokenResponse

    @GET("auth")
    suspend fun authenticate(): Response<Void>
}