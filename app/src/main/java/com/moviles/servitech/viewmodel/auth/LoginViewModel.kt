package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.model.User
import com.moviles.servitech.model.enums.UserRole
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import com.moviles.servitech.services.validation.auth.LoginValidation
import com.moviles.servitech.viewmodel.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the different states of the login process.
 * It can be in a loading state, success state with the login response,
 * or error state with an error message.
 *
 * @property Loading Represents the loading state of the login process.
 * @property Success Represents a successful login with the login response data.
 * @property Error Represents an error state with an error message.
 */
sealed class LoginState {
    object Loading : LoginState()
    data class Success(val data: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * ViewModel for handling user login operations.
 * It manages the login form state, validation, and interactions with the AuthService.
 * It also provides LiveData for the email and password fields,
 * as well as the login state.
 *
 * @property authService The service for handling authentication operations.
 * @property loginValidation The validation service for login fields.
 * @property stringProvider The provider for string resources.
 * @property sessionManager The session manager for handling user sessions.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val loginValidation: LoginValidation,
    private val stringProvider: AndroidStringProvider,
    private val sessionManager: SessionManager
) : ViewModel() {

    // LiveData for the email and password fields, their error states, and the login enable state.
    private val emailState = FieldState<String>()
    val email: LiveData<String> = emailState.data
    val emailError: LiveData<Boolean> = emailState.error
    val emailErrorMsg: LiveData<String?> = emailState.errorMessage

    private val passwordState = FieldState<String>()
    val password: LiveData<String> = passwordState.data
    val passwordError: LiveData<Boolean> = passwordState.error
    val passwordErrorMsg: LiveData<String?> = passwordState.errorMessage

    // LiveData for the login enable state, which is true if both email and password are valid.
    private val _loginEnable = MediatorLiveData<Boolean>().apply {
        addSource(emailState.data) { value = validateForm() }
        addSource(passwordState.data) { value = validateForm() }
    }
    val loginEnable: LiveData<Boolean> = _loginEnable

    // LiveData for the login state, which can be Loading, Success, or Error.
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    /**
     * Event handler for when the email field changes.
     * It updates the email state, validates the email,
     * and updates the error state accordingly using
     * the [LoginValidation] service.
     *
     * @param value The new email value entered by the user.
     */
    fun onEmailChanged(value: String) {
        emailState.data.value = value
        val validation = loginValidation.validateEmail(value)
        emailState.error.value = !validation.isValid
        emailState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the password field changes.
     * It updates the password state, validates the password,
     * and updates the error state accordingly using
     * the [LoginValidation] service.
     *
     * @param value The new password value entered by the user.
     */
    fun onPasswordChanged(value: String) {
        passwordState.data.value = value
        val validation = loginValidation.validatePassword(value)
        passwordState.error.value = !validation.isValid
        passwordState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the login button is selected.
     * It validates the form, resets any previous errors,
     * and attempts to log in using the [AuthService].
     * If successful, it saves the session using the [SessionManager].
     * If there are errors, it updates the error states accordingly.
     */
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

    /**
     * Event handler for when the guest login option is selected.
     * It creates a guest user with predefined values and saves the session.
     * It then updates the login state to indicate success.
     */
    fun onGuestSelected() {
        val guestUser = User(
            id = -1,
            role = UserRole.GUEST.name,
            name = stringProvider.getString(R.string.guest_name),
            email = stringProvider.getString(R.string.guest_email),
            phone = "+000 0000 0000"
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

    /**
     * Validates the login form by checking if the email and password fields are valid.
     * It uses the [LoginValidation] service to perform the validation.
     *
     * @return True if both email and password are valid, false otherwise.
     */
    private fun validateForm(): Boolean {
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        return loginValidation.validateEmail(email).isValid &&
                loginValidation.validatePassword(password).isValid
    }

    /**
     * Resets the error states for the email and password fields.
     * It sets the error values to false and clears any error messages.
     */
    private fun resetErrors() {
        emailState.error.value = false
        emailState.errorMessage.value = null
        passwordState.error.value = false
        passwordState.errorMessage.value = null
    }
}