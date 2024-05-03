package cz.cvut.fit.poberboh.loc_share.network.requests

/**
 * Data class representing a record location request to the server.
 * @property incidentId The ID of the incident.
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 */
data class RecordLocationRequest(
    val incidentId: Long,
    val latitude: Double,
    val longitude: Double
)