package cz.cvut.fit.poberboh.loc_share.network.requests

data class LocationRequest(
    val incidentId: Long,
    val latitude: String,
    val longitude: String
)