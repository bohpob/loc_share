package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.RefreshApi
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class AppAuthenticatorTest {

    private lateinit var refreshApi: RefreshApi
    private lateinit var appPreferences: AppPreferences
    private lateinit var appAuthenticator: AppAuthenticator

    @Before
    fun setup() {
        refreshApi = mockk()
        appPreferences = mockk(relaxed = true)
        appAuthenticator = AppAuthenticator(refreshApi, appPreferences)
    }

    @Test
    fun testAuthenticateSuccess() {
        // Arrange
        val oldRefreshToken = "old_refresh_token"
        val newAccessToken = "new_access_token"
        val newRefreshToken = "new_refresh_token"
        val tokenResponse = TokenResponse(newAccessToken, newRefreshToken)

        val response = mockk<Response>(relaxed = true) {
            every { code } returns 401
            every { request } returns Request.Builder().url("http://test.com").build()
        }

        coEvery { appPreferences.refreshToken } returns flowOf(oldRefreshToken)
        coEvery { refreshApi.refreshToken(any()) } returns tokenResponse

        // Act
        val result = runBlocking { appAuthenticator.authenticate(null, response) }

        // Assert
        coVerify { appPreferences.saveTokens(newAccessToken, newRefreshToken) }
        assertNotNull(result)
        assertEquals("Bearer $newAccessToken", result?.header("Authorization"))
    }

    @Test
    fun testAuthenticateFailure() {
        // Arrange
        val oldRefreshToken = "old_refresh_token"

        val response = mockk<Response>(relaxed = true) {
            every { code } returns 401
            every { request } returns Request.Builder().url("http://test.com").build()
        }

        coEvery { appPreferences.refreshToken } returns flowOf(oldRefreshToken)
        coEvery { refreshApi.refreshToken(any()) } throws HttpException(mockk(relaxed = true))

        // Act
        val result = runBlocking { appAuthenticator.authenticate(null, response) }

        // Assert
        assertNull(result)
    }

    @Test
    fun testAuthenticateNon401Response() {
        // Arrange
        val response = mockk<Response>(relaxed = true) {
            every { code } returns 400
        }

        // Act
        val result = appAuthenticator.authenticate(null, response)

        // Assert
        assertNull(result)
    }
}
