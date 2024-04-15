package cz.cvut.fit.poberboh.loc_share.network.requests

data class RecordLocationRequest(
    val incidentId: Long,
    val latitude: Double,
    val longitude: Double
)