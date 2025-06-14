package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.model.enums.UserRole
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import com.moviles.servitech.services.helpers.ServicesHelper.userHaveRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the different states of the logout process.
 * It can be in a loading state, success state, or error state with an error message.
 *
 * @property Loading Represents the loading state of the logout process.
 * @property Success Represents a successful logout.
 * @property Error Represents an error state with an error message.
 */
sealed class LogoutState {
    object Loading : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

/**
 * ViewModel for handling user logout operations.
 * It manages the logout process and provides LiveData for the logout state.
 *
 * @property authService The service for handling authentication operations.
 * @property sessionManager The session manager for handling user sessions.
 */
@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LiveData for the logout state, which can be Loading, Success, or Error.
    private val _logoutState = MutableLiveData<LogoutState>()
    val logoutState: LiveData<LogoutState> = _logoutState

    /**
     * Initiates the logout process.
     * It checks if the user is a guest and clears the session if so.
     * Otherwise, it calls the AuthService to perform the logout operation.
     *
     * @param token The authentication token of the user.
     */
    fun logout(token: String) {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading

            val user = sessionManager.user.firstOrNull()
            if (userHaveRole(user?.role.orEmpty(), UserRole.GUEST)) {
                sessionManager.clearSession()
                _logoutState.value = LogoutState.Success
                return@launch
            }

            when (val logoutResult = authService.logout(token)) {
                is AuthResult.Success -> {
                    sessionManager.clearSession()
                    _logoutState.value = LogoutState.Success
                }
                is AuthResult.Error -> {
                    _logoutState.value = LogoutState.Error(logoutResult.message)
                }
            }
        }
    }

}