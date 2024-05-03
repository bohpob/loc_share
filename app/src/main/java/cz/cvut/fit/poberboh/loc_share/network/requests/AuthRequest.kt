package cz.cvut.fit.poberboh.loc_share.network.requests

/**
 * Data class representing an authentication request to the server.
 * @property username The username of the user.
 * @property password The password of the user.
 */
data class AuthRequest(
    val username: String,
    val password: String
)
