package cz.cvut.fit.poberboh.loc_share.network.responses

/**
 * Data class representing an incident response from the server.
 * @property id The ID of the incident.
 * @property userId The ID of the user who reported the incident.
 * @property category The category of the incident.
 * @property state The state of the incident.
 * @property note The note associated with the incident.
 */
data class IncidentResponse(
    val id: Long,
    val userId: Long,
    val category: String,
    val state: Boolean,
    val note: String?
)
