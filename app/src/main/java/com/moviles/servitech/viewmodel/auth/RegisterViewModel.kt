package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.common.PhoneUtils.formatPhoneForDisplay
import com.moviles.servitech.common.PhoneUtils.normalizePhoneInput
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.model.User
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import com.moviles.servitech.services.validation.auth.RegisterValidation
import com.moviles.servitech.viewmodel.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the different states of the registration process.
 * It can be in a loading state, success state with the user data,
 * or error state with an error message.
 *
 * @property Loading Represents the loading state of the registration process.
 * @property Success Represents a successful registration with the user data.
 * @property Error Represents an error state with an error message.
 */
sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val data: User?) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

/**
 * ViewModel for handling user registration operations.
 * It manages the registration form state, validation, and interactions with the AuthService.
 * It also provides LiveData for the name, phone, email, password, and password confirmation fields,
 * as well as their error states and the overall registration state.
 *
 * @property authService The service for handling authentication operations.
 * @property registerValidation The validation service for registration fields.
 * @property stringProvider The provider for string resources.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService,
    private val registerValidation: RegisterValidation,
    private val stringProvider: AndroidStringProvider
) : ViewModel() {

    /**
     * LiveData for the name, phone, email, password, and password confirmation fields,
     * and their error states.
     */
    private val nameState = FieldState<String>()
    val name: LiveData<String> = nameState.data
    val nameError: LiveData<Boolean> = nameState.error
    val nameErrorMsg: LiveData<String?> = nameState.errorMessage

    private val phoneState = FieldState<String>()
    val phone: LiveData<String> = phoneState.data
    val phoneError: LiveData<Boolean> = phoneState.error
    val phoneErrorMsg: LiveData<String?> = phoneState.errorMessage

    private val emailState = FieldState<String>()
    val email: LiveData<String> = emailState.data
    val emailError: LiveData<Boolean> = emailState.error
    val emailErrorMsg: LiveData<String?> = emailState.errorMessage

    private val passwordState = FieldState<String>()
    val password: LiveData<String> = passwordState.data
    val passwordError: LiveData<Boolean> = passwordState.error
    val passwordErrorMsg: LiveData<String?> = passwordState.errorMessage

    private val passwordConfirmationState = FieldState<String>()
    val passwordConfirmation: LiveData<String> = passwordConfirmationState.data
    val passwordConfirmationError: LiveData<Boolean> = passwordConfirmationState.error
    val passwordConfirmationErrorMsg: LiveData<String?> = passwordConfirmationState.errorMessage

    /**
     * LiveData for the registration enable state, which is true if all fields are valid.
     * It uses MediatorLiveData to observe changes in the individual field states.
     */
    private val _registerEnable = MediatorLiveData<Boolean>().apply {
        addSource(nameState.data) { value = validateForm() }
        addSource(phoneState.data) { value = validateForm() }
        addSource(emailState.data) { value = validateForm() }
        addSource(passwordState.data) { value = validateForm() }
        addSource(passwordConfirmationState.data) { value = validateForm() }
    }
    val registerEnable: LiveData<Boolean> = _registerEnable

    /**
     * LiveData for the registration state, which can be Loading, Success, or Error.
     * It holds the current state of the registration process.
     */
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    /**
     * Event handler for when the name field changes.
     * It updates the name state, validates the name,
     * and updates the error state accordingly using
     * the [RegisterValidation] service.
     *
     * @param value The new name value entered by the user.
     */
    fun onNameChanged(value: String) {
        nameState.data.value = value
        val validation = registerValidation.validateName(value)
        nameState.error.value = !validation.isValid
        nameState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the phone field changes.
     * It updates the phone state, validates the phone,
     * and updates the error state accordingly using
     * the [RegisterValidation] service.
     *
     * @param value The new phone value entered by the user.
     */
    fun onPhoneChanged(value: String) {
        phoneState.data.value = value
        val validation = registerValidation.validatePhone(value)
        phoneState.error.value = !validation.isValid
        phoneState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the email field changes.
     * It updates the email state, validates the email,
     * and updates the error state accordingly using
     * the [RegisterValidation] service.
     *
     * @param value The new email value entered by the user.
     */
    fun onEmailChanged(value: String) {
        emailState.data.value = value
        val validation = registerValidation.validateEmail(value)
        emailState.error.value = !validation.isValid
        emailState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the password field changes.
     * It updates the password state, validates the password,
     * and updates the error state accordingly using
     * the [RegisterValidation] service.
     *
     * @param value The new password value entered by the user.
     */
    fun onPasswordChanged(value: String) {
        passwordState.data.value = value
        val validation = registerValidation.validatePassword(value)
        passwordState.error.value = !validation.isValid
        passwordState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the password confirmation field changes.
     * It updates the password confirmation state, validates the confirmation,
     * and updates the error state accordingly using
     * the [RegisterValidation] service.
     *
     * @param value The new password confirmation value entered by the user.
     */
    fun onPasswordConfirmationChanged(value: String) {
        passwordConfirmationState.data.value = value
        val validation = registerValidation.validatePasswordConfirmation(
            password = passwordState.data.value.orEmpty(),
            confirmation = value
        )
        passwordConfirmationState.error.value = !validation.isValid
        passwordConfirmationState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the register button is selected.
     * It validates the form, resets any previous errors,
     * and initiates the registration process using the [AuthService].
     */
    fun onRegisterSelected() {
        val name = nameState.data.value.orEmpty()
        val phone = phoneState.data.value.orEmpty()
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        val passwordConfirmation = passwordConfirmationState.data.value.orEmpty()
        resetErrors()

        // Normalize and format the phone number for registration
        val normalized = normalizePhoneInput(phone)
        val formatted = formatPhoneForDisplay(normalized)

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val registerRequest = RegisterRequest(
                name = name,
                phone = formatted,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )

            when (val registerResult = authService.register(registerRequest)) {
                is AuthResult.Success -> {
                    val returnedUser: User? = registerResult.data.user

                    val isRegistered: Boolean = validateResponseEqualsWithRequest(
                        response = returnedUser,
                        request = registerRequest
                    )
                    if (!isRegistered) {
                        _registerState.value = RegisterState.Error(
                            stringProvider.getString(R.string.register_error)
                        )
                    } else {
                        _registerState.value = RegisterState.Success(returnedUser)
                    }
                }
                is AuthResult.Error -> {
                    registerResult.fieldErrors["name"]?.let { errorMsg ->
                        nameState.error.value = true
                        nameState.errorMessage.value = errorMsg
                    }
                    registerResult.fieldErrors["phone"]?.let { errorMsg ->
                        phoneState.error.value = true
                        phoneState.errorMessage.value = errorMsg
                    }
                    registerResult.fieldErrors["email"]?.let { errorMsg ->
                        emailState.error.value = true
                        emailState.errorMessage.value = errorMsg
                    }
                    registerResult.fieldErrors["password"]?.let { errorMsg ->
                        passwordState.error.value = true
                        passwordState.errorMessage.value = errorMsg
                    }
                    registerResult.fieldErrors["password_confirmation"]?.let { errorMsg ->
                        passwordConfirmationState.error.value = true
                        passwordConfirmationState.errorMessage.value = errorMsg
                    }

                    _registerState.value = RegisterState.Error(registerResult.message)
                }
            }
        }
    }

    /**
     * Validates the registration form by checking if all fields are valid.
     * It uses the [RegisterValidation] service to perform the validation.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private fun validateForm(): Boolean {
        val name = nameState.data.value.orEmpty()
        val phone = phoneState.data.value.orEmpty()
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        return registerValidation.validateName(name).isValid &&
                registerValidation.validatePhone(phone).isValid &&
                registerValidation.validateEmail(email).isValid &&
                registerValidation.validatePassword(password).isValid &&
                registerValidation.validatePasswordConfirmation(
                    password = password,
                    confirmation = passwordConfirmationState.data.value.orEmpty()
                ).isValid
    }

    /**
     * Validates that the response from the registration matches the request.
     * It checks if the user ID is valid and if the name, phone, and email
     * in the response match those in the request.
     *
     * @param response The user data returned from the registration API.
     * @param request The original registration request data.
     * @return True if the response matches the request, false otherwise.
     */
    private fun validateResponseEqualsWithRequest(
        response: User?,
        request: RegisterRequest
    ): Boolean {
        if (response == null) return false
        return (response.id != null && response.id > 0) &&
                response.name == request.name &&
                response.phone == request.phone &&
                response.email == request.email
    }

    /**
     * Resets all error states and messages for the registration form fields.
     * This is typically called before starting a new registration attempt
     * to clear any previous validation errors.
     */
    private fun resetErrors() {
        nameState.error.value = false
        nameState.errorMessage.value = null

        phoneState.error.value = false
        phoneState.errorMessage.value = null

        emailState.error.value = false
        emailState.errorMessage.value = null

        passwordState.error.value = false
        passwordState.errorMessage.value = null

        passwordConfirmationState.error.value = false
        passwordConfirmationState.errorMessage.value = null
    }

}