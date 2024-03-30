package cz.cvut.fit.poberboh.loc_share.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import cz.cvut.fit.poberboh.loc_share.repository.AuthRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : BaseViewModel(repository) {

    private val _loginResponse: MutableLiveData<Resource<TokenResponse>> = MutableLiveData()
    private val _registerResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()

    val loginResponse: LiveData<Resource<TokenResponse>> get() = _loginResponse
    val registerResponse: LiveData<Resource<Unit>> get() = _registerResponse

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(username, password)
    }

    suspend fun saveAccessToken(accessToken: String) {
        repository.saveAccessToken(accessToken)
    }

    fun register(username: String, password: String) =
        viewModelScope.launch {
            _registerResponse.value = Resource.Loading
            _registerResponse.value = repository.register(username, password)
        }

    fun authenticate() = viewModelScope.launch {
        repository.authenticate()
    }

    fun getPasswordMismatchWarning(): String {
        return repository.getPasswordMismatchWarning()
    }
}