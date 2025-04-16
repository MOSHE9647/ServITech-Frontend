package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.common.Constants.GUEST_ROLE
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.network.repositories.AuthRepositoryImpl
import com.moviles.servitech.network.repositories.LogoutResult
import com.moviles.servitech.network.services.providers.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val sessionManager: SessionManager,
    private val stringProvider: StringProvider
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

            authRepository.logout(token)
                .onSuccess { logoutResult ->
                    when (logoutResult) {
                        is LogoutResult.Success -> {
                            sessionManager.clearSession()
                            _logoutState.value = LogoutState.Success
                            return@onSuccess
                        }
                        is LogoutResult.Error -> {
                            _logoutState.value = LogoutState.Error(logoutResult.message)
                            return@onSuccess
                        }
                    }
                }
                .onFailure { exception ->
                    _logoutState.value = LogoutState.Error(
                        exception.message ?: stringProvider.getString(R.string.unknown_error)
                    )
                    return@onFailure
                }
        }
    }

    sealed class LogoutState {
        object Loading : LogoutState()
        object Success : LogoutState()
        data class Error(val message: String) : LogoutState()
    }

}