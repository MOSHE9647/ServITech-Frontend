package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.model.User
import com.moviles.servitech.network.repositories.AuthRepositoryImpl
import com.moviles.servitech.network.repositories.RegisterResult
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.services.providers.StringProvider
import com.moviles.servitech.viewmodel.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val stringProvider: StringProvider
) : ViewModel() {

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

    private val _registerEnable = MediatorLiveData<Boolean>().apply {
        addSource(nameState.data) { value = validateForm() }
        addSource(phoneState.data) { value = validateForm() }
        addSource(emailState.data) { value = validateForm() }
        addSource(passwordState.data) { value = validateForm() }
        addSource(passwordConfirmationState.data) { value = validateForm() }
    }
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun onNameChanged(value: String) {
        nameState.data.value = value
        val validation = authRepository.validateName(value)
        nameState.error.value = !validation.isValid
        nameState.errorMessage.value = validation.errorMessage
    }

    fun onPhoneChanged(value: String) {
        phoneState.data.value = value
        val validation = authRepository.validatePhone(value)
        phoneState.error.value = !validation.isValid
        phoneState.errorMessage.value = validation.errorMessage
    }

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

    fun onPasswordConfirmationChanged(value: String) {
        passwordConfirmationState.data.value = value
        val validation = authRepository.validatePasswordConfirmation(
            password = passwordState.data.value.orEmpty(),
            confirmation = value
        )
        passwordConfirmationState.error.value = !validation.isValid
        passwordConfirmationState.errorMessage.value = validation.errorMessage
    }

    fun onRegisterSelected() {
        val name = nameState.data.value.orEmpty()
        val phone = phoneState.data.value.orEmpty()
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        val passwordConfirmation = passwordConfirmationState.data.value.orEmpty()
        resetErrors()

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val registerRequest = RegisterRequest(
                name = name,
                phone = phone,
                email = email,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
            authRepository.register(registerRequest)
                .onSuccess { registerResult ->
                    when (registerResult) {
                        is RegisterResult.Success -> {
                            val returnedUser: User? = registerResult.data.user

                            val isRegistered: Boolean = validateResponseEqualsWithRequest(
                                response = returnedUser,
                                request = registerRequest
                            )
                            if (!isRegistered) _registerState.value = RegisterState.Error(
                                stringProvider.getString(R.string.register_error)
                            )
                            else _registerState.value = RegisterState.Success(returnedUser)
                        }

                        is RegisterResult.Error -> {
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

                            _registerState.value = RegisterState.Error(
                                registerResult.message
                            )
                        }
                    }
                }.onFailure {
                    _registerState.value = RegisterState.Error(
                        it.message ?: stringProvider.getString(R.string.unknown_error)
                    )
                }
        }
    }

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

    private fun validateForm(): Boolean {
        val name = nameState.data.value.orEmpty()
        val phone = phoneState.data.value.orEmpty()
        val email = emailState.data.value.orEmpty()
        val password = passwordState.data.value.orEmpty()
        return authRepository.validateName(name).isValid &&
                authRepository.validatePhone(phone).isValid &&
                authRepository.validateEmail(email).isValid &&
                authRepository.validatePassword(password).isValid &&
                authRepository.validatePasswordConfirmation(
                    password = password,
                    confirmation = passwordConfirmationState.data.value.orEmpty()
                ).isValid
    }

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

    sealed class RegisterState {
        object Loading : RegisterState()
        data class Success(val data: User?) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}