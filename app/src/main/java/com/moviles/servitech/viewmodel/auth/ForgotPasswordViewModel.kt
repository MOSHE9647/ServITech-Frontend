package com.moviles.servitech.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.services.AuthService
import com.moviles.servitech.services.validation.auth.ForgotPasswordValidation
import com.moviles.servitech.viewmodel.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing the different states of the forgot password process.
 * It can be in a loading state, success state with the forgot password response,
 * or error state with an error message.
 *
 * @property Loading Represents the loading state of the forgot password process.
 * @property Success Represents a successful forgot password with the response data.
 * @property Error Represents an error state with an error message.
 */
sealed class ForgotPasswordState {
    object Loading : ForgotPasswordState()
    data class Success(val data: String) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authService: AuthService,
    private val stringProvider: AndroidStringProvider,
    private val forgotPasswordValidation: ForgotPasswordValidation
) : ViewModel() {

    // LiveData for the email field and it error state.
    private val emailState = FieldState<String>()
    val email: LiveData<String> = emailState.data
    val emailError: LiveData<Boolean> = emailState.error
    val emailErrorMsg: LiveData<String?> = emailState.errorMessage

    /**
     * LiveData that determines whether the "Send Link" button should be enabled.
     * It uses a MediatorLiveData to observe changes in the email field and validate the form.
     */
    private val _sendLinkEnable = MediatorLiveData<Boolean>().apply {
        addSource(emailState.data) { value = validateForm() }
    }
    val sendLinkEnable: LiveData<Boolean> = _sendLinkEnable

    // LiveData for the forgot password state, which can be Loading, Success, or Error.
    private val _forgotPasswordState = MediatorLiveData<ForgotPasswordState>()
    val forgotPasswordState: LiveData<ForgotPasswordState> = _forgotPasswordState

    /**
     * Event handler for when the email field changes.
     * It updates the email state, validates the email,
     * and updates the error state accordingly using
     * the [ForgotPasswordValidation] service.
     *
     * @param value The new email value entered by the user.
     */
    fun onEmailChanged(value: String) {
        emailState.data.value = value
        val validation = forgotPasswordValidation.validateEmail(value)
        emailState.error.value = !validation.isValid
        emailState.errorMessage.value = validation.errorMessage
    }

    /**
     * Event handler for when the "Send Link" button is clicked.
     * It resets the error state, validates the form,
     * and calls the [AuthService] to reset the password.
     * If successful, it updates the forgot password state to Success,
     * otherwise it updates it to Error with the appropriate message.
     */
    fun onSendLinkClicked() {
        val email = emailState.data.value.orEmpty()
        resetErrorState()

        viewModelScope.launch {
            _forgotPasswordState.value = ForgotPasswordState.Loading

            when (val result = authService.resetPassword(email)) {
                is AuthResult.Success -> {
                    _forgotPasswordState.value = ForgotPasswordState.Success(
                        stringProvider.getString(R.string.forgot_password_success)
                    )
                }

                is AuthResult.Error -> {
                    result.fieldErrors["email"]?.let { errorMsg ->
                        emailState.error.value = true
                        emailState.errorMessage.value = errorMsg
                    }
                    _forgotPasswordState.value = ForgotPasswordState.Error(result.message)
                }
            }
        }
    }

    /**
     * Validates the forgot password form by checking if the email field is valid.
     * It uses the [ForgotPasswordValidation] service to perform the validation.
     *
     * @return True if email is valid, false otherwise.
     */
    private fun validateForm(): Boolean {
        val email = emailState.data.value.orEmpty()
        return forgotPasswordValidation.validateEmail(email).isValid
    }

    /**
     * Resets the error state of the email field.
     * It sets the error flag to false and clears the error message.
     */
    private fun resetErrorState() {
        emailState.error.value = false
        emailState.errorMessage.value = null
    }

}