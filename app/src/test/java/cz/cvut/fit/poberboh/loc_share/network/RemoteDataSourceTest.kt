package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.RefreshApi
import cz.cvut.fit.poberboh.loc_share.network.requests.RefreshToken
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var refreshApi: RefreshApi
    private lateinit var appPreferences: AppPreferences

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        appPreferences = mockk(relaxed = true)

        // Create an instance of RemoteDataSource
        remoteDataSource = RemoteDataSource()

        // Create an instance of RefreshApi using MockWebServer as the base URL
        refreshApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testRefreshTokenSuccess() {
        // Arrange
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"accessToken": "new_access_token", "refreshToken": "new_refresh_token"}""")
        mockWebServer.enqueue(mockResponse)

        val refreshToken = RefreshToken("old_refresh_token")

        // Act
        val result = runBlocking {
            refreshApi.refreshToken(refreshToken)
        }

        // Assert
        val request = mockWebServer.takeRequest()
        assertEquals("/auth/refresh", request.path)
        assertEquals("POST", request.method)

        val expectedResponse = TokenResponse("new_access_token", "new_refresh_token")
        assertEquals(expectedResponse, result)
    }

    @Test
    fun testRefreshTokenFailure() {
        // Arrange
        val mockResponse = MockResponse().setResponseCode(500) // Internal server error
        mockWebServer.enqueue(mockResponse)

        val refreshToken = RefreshToken("old_refresh_token")

        // Act
        val result = runBlocking {
            try {
                refreshApi.refreshToken(refreshToken)
            } catch (e: HttpException) {
                null // Return null for simplicity
            }
        }

        // Assert
        val request = mockWebServer.takeRequest()
        assertEquals("/auth/refresh", request.path)
        assertEquals("POST", request.method)

        assertNull(result)
    }

    @Test
    fun testRefreshTokenFailureWithHTTP404Response() {
        // Arrange
        val mockResponse = MockResponse().setResponseCode(404) // Not Found
        mockWebServer.enqueue(mockResponse)

        val refreshToken = RefreshToken("old_refresh_token")

        // Act
        val result = runBlocking {
            try {
                refreshApi.refreshToken(refreshToken)
            } catch (e: HttpException) {
                null // Return null for simplicity
            }
        }

        // Assert
        val request = mockWebServer.takeRequest()
        assertEquals("/auth/refresh", request.path)
        assertEquals("POST", request.method)

        assertNull(result)
    }
}
