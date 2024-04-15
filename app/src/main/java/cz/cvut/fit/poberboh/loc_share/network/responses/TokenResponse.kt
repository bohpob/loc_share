package cz.cvut.fit.poberboh.loc_share.network.responses

data class TokenResponse(
    val accessToken: String?,
    val refreshToken: String?
)
