package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.RefreshToken
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {
    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): TokenResponse
}