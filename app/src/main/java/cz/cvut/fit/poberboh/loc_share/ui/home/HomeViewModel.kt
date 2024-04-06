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
    private var timer: Timer? = null

    fun getUsername() = viewModelScope.launch {
        if (_user.value == null) {
            _user.value = Resource.Loading
            _user.value = repository.getUsername()
        }
    }

    fun createIncident() = viewModelScope.launch {
        if (_selectedCategory.value != null) {
            _incident.value = repository.createIncident(
                _selectedCategory.value!!,
                _note.value
            )
        }
    }

    fun handleIncident(incidentId: Long) = viewModelScope.launch {
        _incidentId.value = incidentId
        startRequestTimer()
        _button.value = repository.getRedButtonProperties()
    }

    fun stopShare() = viewModelScope.launch {
        if (_incidentId.value != null) {
            _stop.value = repository.stopShare(_incidentId.value!!)
        }
    }

    fun handleStopShare() = viewModelScope.launch {
        _incidentId.value = null
        stopRequestTimer()
        _button.value = repository.getGreenButtonProperties()
    }

    fun recordLocation() = viewModelScope.launch {
        repository.recordLocation(
            _incidentId.value!!,
            _location.value?.latitude.toString(),
            _location.value?.longitude.toString()
        )
    }

    fun updateCurrentLocation(point: GeoPoint) = viewModelScope.launch {
        _location.value = point
    }

    fun logout() = viewModelScope.launch {
        if (!_button.value!!.first && _incidentId.value != null) {
            repository.stopShare(_incidentId.value!!)
        }
    }

    fun toggleButton() {
        if (_button.value!!.first) {
            createIncident()
        } else {
            stopShare()
        }
    }

    private fun startRequestTimer() {
        stopRequestTimer()

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                recordLocation()
            }
        }, 3000, 3000)
    }

    private fun stopRequestTimer() {
        timer?.cancel()
        timer = null
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setEnteredText(text: String) {
        _note.value = text
    }

    fun getUsernameForm(): String {
        return repository.getUsernameForm()
    }
}