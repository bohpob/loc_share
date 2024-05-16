package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.api.AuthApi
import cz.cvut.fit.poberboh.loc_share.network.requests.AuthRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var authApi: AuthApi
    private lateinit var appPreferences: AppPreferences
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authApi = mockk()
        appPreferences = mockk(relaxed = true)  // relaxed = true to avoid providing mocks for each method
        authRepository = AuthRepository(authApi, appPreferences)
    }

    @Test
    fun testRegisterSuccess() {
        // Arrange
        val username = "test_user"
        val password = "test_password"
        coEvery { authApi.register(any()) } returns Unit

        // Act
        val result: Resource<Unit>
        runBlocking { result = authRepository.register(username, password) }

        // Assert
        coVerify { authApi.register(AuthRequest(username, password)) }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun testLoginSuccess() {
        // Arrange
        val username = "test_user"
        val password = "test_password"
        val authResponse = TokenResponse("access_token", "refresh_token")
        coEvery { authApi.login(any()) } returns authResponse

        // Act
        val result: Resource<TokenResponse>
        runBlocking { result = authRepository.login(username, password) }

        // Assert
        coVerify { authApi.login(AuthRequest(username, password)) }
        assertTrue(result is Resource.Success)
        assertEquals(authResponse, (result as Resource.Success).data)
    }

    @Test
    fun testSaveTokens() {
        // Arrange
        val accessToken = "test_access_token"
        val refreshToken = "test_refresh_token"

        // Act
        runBlocking { authRepository.saveTokens(accessToken, refreshToken) }

        // Assert
        verify { runBlocking { appPreferences.saveTokens(accessToken, refreshToken) } }
    }

    @Test
    fun testGetPasswordMismatchWarning() {
        // Arrange
        val warningMessage = "Passwords do not match"
        every { appPreferences.getPasswordMismatchWarning() } returns warningMessage

        // Act
        val result = authRepository.getPasswordMismatchWarning()

        // Assert
        assertEquals(warningMessage, result)
    }
}
