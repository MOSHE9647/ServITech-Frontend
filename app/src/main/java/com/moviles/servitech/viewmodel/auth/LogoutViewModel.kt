package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.common.Constants.GUEST_ROLE
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LogoutState {
    object Loading : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _logoutState = MutableLiveData<LogoutState>()
    val logoutState: LiveData<LogoutState> = _logoutState

    fun logout(token: String) {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading

            val user = sessionManager.user.firstOrNull()
            if (user?.role == GUEST_ROLE) {
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