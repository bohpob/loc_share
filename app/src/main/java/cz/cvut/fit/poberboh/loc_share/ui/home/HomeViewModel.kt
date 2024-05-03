package cz.cvut.fit.poberboh.loc_share.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.responses.IncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Timer
import java.util.TimerTask

/**
 * ViewModel class for the HomeFragment and MapFragment.
 * This class is responsible for handling the data for the HomeFragment, MapFragment and for communicating with the repository.
 *
 * @param repository The repository to be associated with the ViewModel.
 */
class HomeViewModel(private val repository: BasicRepository) : BaseViewModel(repository) {
    private val _location: MutableLiveData<GeoPoint> = MutableLiveData()
    private val _incidentId: MutableLiveData<Long?> = MutableLiveData()
    private val _user: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    private val _incident: MutableLiveData<Resource<IncidentResponse>> = MutableLiveData()
    private val _stop: MutableLiveData<Resource<Unit>> = MutableLiveData()
    private val _selectedCategory: MutableLiveData<String> = MutableLiveData()
    private val _note: MutableLiveData<String?> = MutableLiveData()
    private val _categories: MutableLiveData<List<String>> =
        MutableLiveData<List<String>>().apply { value = repository.getCategoriesFromResources() }
    private val _button: MutableLiveData<Triple<Boolean, Int, String>> =
        MutableLiveData<Triple<Boolean, Int, String>>().apply {
            value = repository.getGreenButtonProperties()
        }

    val stop: LiveData<Resource<Unit>> get() = _stop
    val incident: LiveData<Resource<IncidentResponse>> get() = _incident
    val button: LiveData<Triple<Boolean, Int, String>> get() = _button
    val user: LiveData<Resource<UserResponse>> get() = _user
    val selectedCategory: LiveData<String> get() = _selectedCategory
    val categories: LiveData<List<String>> get() = _categories
    // The timer for sending location updates
    private var timer: Timer? = null
    // The interval for sending location updates in milliseconds
    private val locationUpdateInterval = 1000L

    /**
     * Get the username of the user.
     */
    fun getUsername() = viewModelScope.launch {
        if (_user.value == null) {
            _user.value = Resource.Loading // Set the loading state
            _user.value = repository.getUsername()
        }
    }

    /**
     * Create an incident with the selected category and note.
     */
    fun createIncident() = viewModelScope.launch {
        if (_selectedCategory.value != null) {
            _incident.value = repository.createIncident(
                _selectedCategory.value!!,
                _note.value
            )
        }
    }

    /**
     * Handle the incident with the given ID.
     * @param incidentId The ID of the incident to handle.
     */
    fun handleIncident(incidentId: Long) = viewModelScope.launch {
        _incidentId.value = incidentId
    }

    /**
     * Stop sharing the incident.
     */
    fun stopShare() = viewModelScope.launch {
        if (_incidentId.value != null) {
            _stop.value = repository.stopShare(_incidentId.value!!)
            _incidentId.value = null
        }
    }

    /**
     * Record the current location.
     */
    fun recordLocation() = viewModelScope.launch {
        if (_incidentId.value != null &&
            _location.value?.latitude != null &&
            _location.value?.longitude != null
        ) {
            // Record the location
            repository.recordLocation(
                _incidentId.value!!,
                _location.value?.latitude!!,
                _location.value?.longitude!!
            )
        }
    }

    /**
     * Update the current location.
     * @param point The new location.
     */
    fun updateCurrentLocation(point: GeoPoint) = viewModelScope.launch {
        _location.value = point
    }

    /**
     * Log out the user.
     */
    fun logout() = viewModelScope.launch {
        if (!_button.value!!.first && _incidentId.value != null) {
            repository.stopShare(_incidentId.value!!)
        }
    }

    /**
     * Toggle the button state.
     */
    fun toggleButton() {
        if (_button.value!!.first) {
            createIncident()
            startRequestTimer()
            _button.value = repository.getRedButtonProperties()
        } else {
            stopRequestTimer()
            stopShare()
            _button.value = repository.getGreenButtonProperties()
        }
    }

    /**
     * Start the timer for sending location updates.
     */
    private fun startRequestTimer() {
        stopRequestTimer()

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                recordLocation()
            }
        }, locationUpdateInterval, locationUpdateInterval)
    }

    /**
     * Stop the timer for sending location updates.
     */
    private fun stopRequestTimer() {
        timer?.cancel()
        timer = null
    }

    /**
     * Set the selected category for the incident.
     * @param category The category to set.
     */
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    /**
     * Set the entered text for the note.
     * @param text The text to set.
     */
    fun setEnteredText(text: String) {
        _note.value = text
    }

    /**
     * Get the username form from the repository. Example "Username:". Used for the UI.
     * @return The username form.
     */
    fun getUsernameForm(): String {
        return repository.getUsernameForm()
    }
}