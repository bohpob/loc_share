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

/**
 * Interface for the basic API.
 */
interface BasicApi {
    /**
     * Gets the username of the current user.
     * @return The username of the current user.
     */
    @GET("api/v1/users/me")
    suspend fun getUsername(): UserResponse

    /**
     * Creates a new incident.
     * @param request The incident request.
     * @return The incident response.
     */
    @POST("api/v1/incidents")
    suspend fun createIncident(@Body request: IncidentRequest): IncidentResponse

    /**
     * Records the location of the current user.
     * @param request The location request.
     */
    @POST("api/v1/incidents/locations")
    suspend fun recordLocation(@Body request: RecordLocationRequest): Unit

    /**
     * Stops sharing the location of the current user.
     * @param id The ID of the incident.
     */
    @DELETE("api/v1/incidents/{id}")
    suspend fun stopShare(@Path("id") id: Long): Unit
}