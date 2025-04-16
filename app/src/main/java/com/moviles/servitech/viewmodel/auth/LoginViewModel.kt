package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.common.Constants.GUEST_ROLE
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.model.User
import com.moviles.servitech.network.repositories.AuthRepositoryImpl
import com.moviles.servitech.network.repositories.LoginResult
import com.moviles.servitech.network.responses.LoginResponse
import com.moviles.servitech.network.services.providers.StringProvider
import com.moviles.servitech.viewmodel.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val stringProvider: StringProvider,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val emailState = FieldState<String>()
    val email: LiveData<String> = emailState.data
    val emailError: LiveData<Boolean> = emailState.error
    val emailErrorMsg: LiveData<String?> = emailState.errorMessage

    private val passwordState = FieldState<String>()
    val password: LiveData<String> = passwordState.data
    val passwordError: LiveData<Boolean> = passwordState.error
    val passwordErrorMsg: LiveData<String?> = passwordState.errorMessage

    private val _loginEnable = MediatorLiveData<Boolean>().apply {
        addSource(emailState.data) { value = validateForm() }
        addSource(passwordState.data) { value = validateForm() }
    }
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun onEmailChanged(value: String) {
        emailState.data.value = value
        val validation = authRepository.validateEmail(value)
        emailState.error.value = !validation.isValid
        emailState.errorMessage.value = validation.errorMessage
    }

    fun onPasswordChanged(value: String) {
        passwordState.data.value = value
        val validation = authRepository.validatePassword(value)
        passwordState.error.value = !validation.isValid
        passwordState.errorMessage.value = validation.errorMessage
    }

    fun onLoginSelected() {
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        resetErrors()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            authRepository.login(email, password)
                .onSuccess { loginResult ->
                    when (loginResult) {
                        is LoginResult.Success -> {
                            _loginState.value = LoginState.Success(loginResult.data)
                            sessionManager.saveSession(
                                token = loginResult.data.token,
                                expiresIn = loginResult.data.expiresIn,
                                user = loginResult.data.user
                            )
                            return@onSuccess
                        }
                        is LoginResult.Error -> {
                            loginResult.fieldErrors["email"]?.let { errorMsg ->
                                emailState.error.value = true
                                emailState.errorMessage.value = errorMsg
                            }
                            loginResult.fieldErrors["password"]?.let { errorMsg ->
                                passwordState.error.value = true
                                passwordState.errorMessage.value = errorMsg
                            }
                            _loginState.value = LoginState.Error(
                                loginResult.message
                            )
                            return@onSuccess
                        }
                    }
                }
                .onFailure { exception ->
                    _loginState.value = LoginState.Error(
                        exception.message ?: stringProvider.getString(R.string.unknown_error)
                    )
                    return@onFailure
                }
        }
    }

    fun onGuestSelected() {
        val guestUser = User(
            id = -1,
            role = GUEST_ROLE,
            name = stringProvider.getString(R.string.guest_name),
            email = stringProvider.getString(R.string.guest_email),
            phone = "+000 0000 0000"
        )

        val guestLoginResponse = LoginResponse(
            user = guestUser,
            token = "guest_token",
            expiresIn = 3600L
        )

        resetErrors()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            sessionManager.saveSession(
                user = guestLoginResponse.user,
                token = guestLoginResponse.token,
                expiresIn = guestLoginResponse.expiresIn
            )

            _loginState.value = LoginState.Success(guestLoginResponse)
        }
    }

    private fun validateForm(): Boolean {
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        return authRepository.validateEmail(email).isValid &&
                authRepository.validatePassword(password).isValid
    }

    private fun resetErrors() {
        emailState.error.value = false
        emailState.errorMessage.value = null
        passwordState.error.value = false
        passwordState.errorMessage.value = null
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val data: LoginResponse) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}