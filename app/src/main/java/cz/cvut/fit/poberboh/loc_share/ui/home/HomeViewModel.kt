package cz.cvut.fit.poberboh.loc_share.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.requests.GPSIncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.requests.IncidentRequest
import cz.cvut.fit.poberboh.loc_share.network.responses.GPSIncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.IncidentResponse
import cz.cvut.fit.poberboh.loc_share.network.responses.UserResponse
import cz.cvut.fit.poberboh.loc_share.repository.BasicRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class HomeViewModel(
    private val repository: BasicRepository
) : BaseViewModel(repository) {

    private var _currentIncidentId: MutableLiveData<Long> = MutableLiveData()
    private val _user: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    private val _incident: MutableLiveData<Resource<IncidentResponse>> = MutableLiveData()
    private val _gpsIncident: MutableLiveData<Resource<GPSIncidentResponse>> = MutableLiveData()
    private val _stop: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private val _selectedCategory: MutableLiveData<String> = MutableLiveData()
    private val _enteredText: MutableLiveData<String> =
        MutableLiveData<String>().apply { value = "" }
    private val _categories: MutableLiveData<List<String>> =
        MutableLiveData<List<String>>().apply { value = repository.getCategoriesFromResources() }
    private val _button: MutableLiveData<Triple<Boolean, Int, String>> =
        MutableLiveData<Triple<Boolean, Int, String>>().apply {
            value = repository.getGreenButtonProperties()
        }

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

    private fun createIncident(incident: IncidentRequest) = viewModelScope.launch {
        _incident.value = repository.createIncident(incident)
        when (val result = _incident.value) {
            is Resource.Success -> setCurrentIncidentId(result.data.id)
            else -> setCurrentIncidentId(null)
        }
    }

    private fun setCurrentIncidentId(id: Long?) {
        _currentIncidentId.value = id ?: return
    }

    fun createGPSIncident(id: Long, gpsIncident: GPSIncidentRequest) = viewModelScope.launch {
        _gpsIncident.value = repository.createGPSIncident(id.toString(), gpsIncident)
    }

    private fun stopShare() = viewModelScope.launch {
        when (val result = _incident.value) {
            is Resource.Success -> setCurrentIncidentId(result.data.id)
            else -> setCurrentIncidentId(null)
        }

        _stop.value = repository.stop(_currentIncidentId.value!!.toString())
    }

    fun logout() = viewModelScope.launch {
        if (!_button.value!!.first && _currentIncidentId.value != null) {
            repository.stop(_currentIncidentId.value!!.toString())
        }
    }

    fun toggleButton() {
        if (_button.value!!.first) {
            val incidentRequest = IncidentRequest(
                _selectedCategory.value!!,
                _enteredText.value
            )
            createIncident(incidentRequest)
            startRequestTimer()
            _button.value = repository.getRedButtonProperties()
        } else {
            stopShare()
            stopRequestTimer()
            _button.value = repository.getGreenButtonProperties()
        }
    }

    private fun startRequestTimer() {
        stopRequestTimer()

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                createGPSIncident(
                    _currentIncidentId.value!!,
                    GPSIncidentRequest("2", "2") //@todo
                )
            }
        }, 5000, 5000)
    }

    private fun stopRequestTimer() {
        timer?.cancel()
        timer = null
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setEnteredText(text: String) {
        _enteredText.value = text
    }

    fun getUsernameForm(): String {
        return repository.getUsernameForm()
    }
}