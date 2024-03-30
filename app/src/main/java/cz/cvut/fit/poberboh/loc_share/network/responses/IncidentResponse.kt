package cz.cvut.fit.poberboh.loc_share.network.responses

data class IncidentResponse(
    val id: Long,
    val userId: Long,
    val category: String,
    val state: Boolean,
    val note: String?
)
