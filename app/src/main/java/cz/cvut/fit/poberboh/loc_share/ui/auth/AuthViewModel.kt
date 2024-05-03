package cz.cvut.fit.poberboh.loc_share.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fit.poberboh.loc_share.network.Resource
import cz.cvut.fit.poberboh.loc_share.network.responses.TokenResponse
import cz.cvut.fit.poberboh.loc_share.repository.AuthRepository
import cz.cvut.fit.poberboh.loc_share.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel class for the AuthFragment.
 * This class is responsible for handling the data for the AuthFragment and for communicating with the repository.
 *
 * @param repository The repository to be associated with the ViewModel.
 */
class AuthViewModel(private val repository: AuthRepository) : BaseViewModel(repository) {
    private val _loginResponse: MutableLiveData<Resource<TokenResponse>> = MutableLiveData()
    private val _registerResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()

    val loginResponse: LiveData<Resource<TokenResponse>> get() = _loginResponse
    val registerResponse: LiveData<Resource<Unit>> get() = _registerResponse

    /**
     * Attempt to log in with the provided username and password.
     *
     * @param username The username to log in with.
     * @param password The password to log in with.
     */
    fun login(username: String, password: String) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading // Set the loading state
        _loginResponse.value = repository.login(username, password)
    }

    /**
     * Validate the provided username and password.
     *
     * @param username The username to validate.
     * @param password The password to validate.
     * @return A warning message if the input is invalid, or null if the input is valid.
     */
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

    /**
     * Save the provided access and refresh tokens.
     *
     * @param accessToken The access token to save.
     * @param refreshToken The refresh token to save.
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        repository.saveTokens(accessToken, refreshToken)
    }

    /**
     * Register a new user with the provided username and password.
     *
     * @param username The username to register with.
     * @param password The password to register with.
     */
    fun register(username: String, password: String) =
        viewModelScope.launch {
            _registerResponse.value = Resource.Loading
            _registerResponse.value = repository.register(username, password)
        }

    /**
     * Get the warning message for a password mismatch.
     *
     * @return The warning message for a password mismatch.
     */
    fun getPasswordMismatchWarning(): String {
        return repository.getPasswordMismatchWarning()
    }
}