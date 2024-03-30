package cz.cvut.fit.poberboh.loc_share.network.requests

data class IncidentRequest(
    val category: String,
    val note: String?
)