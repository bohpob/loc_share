package cz.cvut.fit.poberboh.loc_share.network.requests

/**
 * Data class representing a refresh token request to the server.
 * @property refreshToken The refresh token.
 */
data class RefreshToken(
    val refreshToken: String?
)
