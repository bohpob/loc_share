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

    fun validateLoginInput(username: String, password: String): String? {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username and password must not be empty"
        } else if (password.length < 8) {
            return "Password must be at least 8 characters"
        } else if (username.length !in 1..25) {
            return "Username must be between 1 and 25 characters"
        } else if (password.contains(" ") || username.contains(" ")) {
            return "Username and password must not contain spaces"
        } else if (!username.matches(Regex("^[a-zA-Z0-9]*$")) ||
            !password.matches(Regex("^[a-zA-Z0-9]*$"))
        ) {
            return "Username must contain only english letters and numbers"
        }
        return null
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        repository.saveTokens(accessToken, refreshToken)
    }

    fun register(username: String, password: String) =
        viewModelScope.launch {
            _registerResponse.value = Resource.Loading
            _registerResponse.value = repository.register(username, password)
        }

    fun getPasswordMismatchWarning(): String {
        return repository.getPasswordMismatchWarning()
    }
}