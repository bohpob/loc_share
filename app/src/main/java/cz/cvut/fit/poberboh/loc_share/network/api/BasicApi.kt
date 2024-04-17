package cz.cvut.fit.poberboh.loc_share.network.api

import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.RecordLocationRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.IncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BasicApi {
    @GET("api/v1/users/me")
    suspend fun getUsername(): UserResponse

    @POST("api/v1/incidents")
    suspend fun createIncident(@Body request: IncidentRequest): IncidentResponse

    @POST("api/v1/incidents/locations")
    suspend fun recordLocation(@Body request: RecordLocationRequest): Unit

    @DELETE("api/v1/incidents/{id}")
    suspend fun stopShare(@Path("id") id: Long): Unit
}