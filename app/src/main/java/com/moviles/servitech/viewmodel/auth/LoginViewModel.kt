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
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.core.providers.StringProvider
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import com.moviles.servitech.services.validation.LoginValidation
import com.moviles.servitech.viewmodel.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val data: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val loginValidation: LoginValidation,
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
        val validation = loginValidation.validateEmail(value)
        emailState.error.value = !validation.isValid
        emailState.errorMessage.value = validation.errorMessage
    }

    fun onPasswordChanged(value: String) {
        passwordState.data.value = value
        val validation = loginValidation.validatePassword(value)
        passwordState.error.value = !validation.isValid
        passwordState.errorMessage.value = validation.errorMessage
    }

    fun onLoginSelected() {
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        resetErrors()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val loginRequest = LoginRequest(email, password)

            when (val loginResult = authService.login(loginRequest)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginState.Success(loginResult.data)
                    sessionManager.saveSession(
                        token = loginResult.data.token,
                        expiresIn = loginResult.data.expiresIn,
                        user = loginResult.data.user
                    )
                }
                is AuthResult.Error -> {
                    loginResult.fieldErrors["email"]?.let { errorMsg ->
                        emailState.error.value = true
                        emailState.errorMessage.value = errorMsg
                    }
                    loginResult.fieldErrors["password"]?.let { errorMsg ->
                        passwordState.error.value = true
                        passwordState.errorMessage.value = errorMsg
                    }
                    _loginState.value = LoginState.Error(loginResult.message)
                }
            }
        }
    }

    fun onGuestSelected() {
        val guestUser = User(
            id = -1,
            role = GUEST_ROLE,
            name = stringProvider.getString(R.string.guest_name),
            email = stringProvider.getString(R.string.guest_email),
            phone = "+000 0000 0000" // This value can be a constant
        )

        resetErrors()

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            sessionManager.saveSession(
                user = guestUser,
                token = "guest_token",
                expiresIn = 3600L
            )

            _loginState.value = LoginState.Success(
                LoginResponse(
                    user = guestUser,
                    token = "guest_token",
                    expiresIn = 3600L
                )
            )
        }
    }

    private fun validateForm(): Boolean {
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        return loginValidation.validateEmail(email).isValid &&
                loginValidation.validatePassword(password).isValid
    }

    private fun resetErrors() {
        emailState.error.value = false
        emailState.errorMessage.value = null
        passwordState.error.value = false
        passwordState.errorMessage.value = null
    }
}