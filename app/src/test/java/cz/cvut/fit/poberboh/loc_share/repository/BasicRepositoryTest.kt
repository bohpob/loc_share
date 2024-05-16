package cz.cvut.fit.poberboh.loc_share.repository

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.api.BasicApi
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.RecordLocationRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.IncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BasicRepositoryTest {

    private lateinit var basicApi: BasicApi
    private lateinit var appPreferences: AppPreferences
    private lateinit var basicRepository: BasicRepository

    @Before
    fun setup() {
        basicApi = mockk()
        appPreferences = mockk(relaxed = true) // relaxed = true to avoid providing mocks for each method
        basicRepository = BasicRepository(basicApi, appPreferences)
    }

    @Test
    fun testGetUsername() {
        // Arrange
        val username = UserResponse("test_user")
        coEvery { basicApi.getUsername() } returns username

        // Act
        val result: Resource<UserResponse>
        runBlocking { result = basicRepository.getUsername() }

        // Assert
        coVerify { basicApi.getUsername() }
        assertTrue(result is Resource.Success)
        assertEquals(username, (result as Resource.Success).data)
    }

    @Test
    fun testCreateIncident() {
        // Arrange
        val category = "test_category"
        val text = "test_text"
        val incident = IncidentResponse(1, 1, category, true, text)
        coEvery { basicApi.createIncident(any()) } returns incident

        // Act
        val result: Resource<IncidentResponse>
        runBlocking { result = basicRepository.createIncident(category, text) }

        // Assert
        coVerify { basicApi.createIncident(IncidentRequest(category, text)) }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun testRecordLocation() {
        // Arrange
        val incidentId = 1L
        val latitude = 50.0
        val longitude = 14.0
        coEvery { basicApi.recordLocation(any()) } returns Unit

        // Act
        val result: Resource<Unit>
        runBlocking { result = basicRepository.recordLocation(incidentId, latitude, longitude) }

        // Assert
        coVerify {
            basicApi.recordLocation(
                RecordLocationRequest(
                    incidentId = incidentId,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun testStopShare() {
        // Arrange
        val id = 1L
        coEvery { basicApi.stopShare(id) } returns Unit

        // Act
        val result: Resource<Unit>
        runBlocking { result = basicRepository.stopShare(id) }

        // Assert
        coVerify { basicApi.stopShare(id) }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun testGetCategoriesFromResources() {
        // Arrange
        val categories = listOf("category1", "category2")
        every { appPreferences.getCategoriesFromResources() } returns categories

        // Act
        val result = basicRepository.getCategoriesFromResources()

        // Assert
        assertEquals(categories, result)
    }

    @Test
    fun testGetRedButtonProperties() {
        // Arrange
        val properties = Triple(true, 1, "red")
        every { appPreferences.getRedButtonProperties() } returns properties

        // Act
        val result = basicRepository.getRedButtonProperties()

        // Assert
        assertEquals(properties, result)
    }

    @Test
    fun testGetGreenButtonProperties() {
        // Arrange
        val properties = Triple(true, 2, "green")
        every { appPreferences.getGreenButtonProperties() } returns properties

        // Act
        val result = basicRepository.getGreenButtonProperties()

        // Assert
        assertEquals(properties, result)
    }

    @Test
    fun testGetUsernameForm() {
        // Arrange
        val usernameForm = "username_form"
        every { appPreferences.getUsernameForm() } returns usernameForm

        // Act
        val result = basicRepository.getUsernameForm()

        // Assert
        assertEquals(usernameForm, result)
    }
}
