package cz.cvut.fit.poberboh.loc_share.network.responses

/**
 * Data class representing a token response from the server.
 * @property accessToken The access token.
 * @property refreshToken The refresh token.
 */
data class TokenResponse(
    val accessToken: String?,
    val refreshToken: String?
)
