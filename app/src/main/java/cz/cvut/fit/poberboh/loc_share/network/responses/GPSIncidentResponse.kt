package cz.cvut.fit.poberboh.loc_share.network.responses

data class GPSIncidentResponse(
    val id: Long,
    val incidentId: Long,
    val gpsX: String,
    val gpsY: String,
    val timestamp: Long
)
