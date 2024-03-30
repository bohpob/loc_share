package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.GPSIncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.GPSIncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.IncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BasicApi {
    @GET("api/v1/me")
    suspend fun getUsername(): UserResponse

    @POST("api/v1/incidents/new")
    suspend fun createIncident(@Body request: IncidentRequest): IncidentResponse

    @POST("api/v1/incidents/{id}")
    suspend fun createGPSIncident(
        @Path("id") id: String,
        @Body request: GPSIncidentRequest
    ): GPSIncidentResponse

    @PATCH("api/v1/incidents/switch/{id}")
    suspend fun stop(@Path("id") id: String): Boolean
}