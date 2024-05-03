package cz.cvut.fit.poberboh.loc_share.network.requests

/**
 * Data class representing an incident request to the server.
 * @property category The category of the incident.
 * @property note The note of the incident.
 */
data class IncidentRequest(
    val category: String,
    val note: String?
)