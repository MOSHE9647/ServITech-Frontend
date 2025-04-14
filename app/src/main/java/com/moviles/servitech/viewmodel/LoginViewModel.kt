package com.moviles.servitech.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.R
import com.moviles.servitech.network.services.AuthService
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.network.responses.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: AuthService
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _emailError = MutableLiveData<Boolean>()
    val emailError : LiveData<Boolean> = _emailError

    private val _emailErrorMsg = MutableLiveData<String>()
    val emailErrorMsg : LiveData<String> = _emailErrorMsg

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _passwordError = MutableLiveData<Boolean>()
    val passwordError : LiveData<Boolean> = _passwordError

    private val _passwordErrorMsg = MutableLiveData<String>()
    val passwordErrorMsg : LiveData<String> = _passwordErrorMsg

    private val _loginEnable = MediatorLiveData<Boolean>().apply {
        addSource(_email) { value = validateForm() }
        addSource(_password) { value = validateForm() }
    }
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private fun validateForm(): Boolean {
        val email= _email.value.orEmpty()
        val password = _password.value.orEmpty()
        return isValidEmail(email) && isValidPassword(password)
    }

    fun onEmailChanged(value: String) {
        _email.value = value
        _emailError.value = value.isNotEmpty() && !isValidEmail(value)
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
        _passwordError.value = value.isNotEmpty() && !isValidPassword(value)
    }

    private fun isValidPassword(password: String): Boolean = password.length > 8

    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun onLoginSelected(context: Context) {
        val loginRequest = LoginRequest(
            email = _email.value.orEmpty(),
            password = _password.value.orEmpty()
        )

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = apiService.login(loginRequest)

                if (response.isSuccessful) {
                    val apiResponse = response.body()!!
                    if (apiResponse.data != null) {
                        _loginState.value = LoginState.Success(apiResponse)
                    } else {
                        _loginState.value = LoginState.Error(apiResponse.message)
                    }
                } else {
                    // HTTP Error Managing (401, 500, etc.)
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                        _loginState.value = LoginState.Error(errorResponse.message)

                        val errors = errorResponse.errors
                        Log.d("LoginViewModel", "Errors: $errors")
                        if (errors.isNotEmpty()) {
                            if (errors.containsKey("email")) {
                                _emailError.value = true
                                _emailErrorMsg.value = errors["email"]
                            }
                            if (errors.containsKey("password")) {
                                _passwordError.value = true
                                _passwordErrorMsg.value = errors["password"]
                            }
                        }
                    } else {
                        _loginState.value = LoginState.Error(getString(context, R.string.unknown_error))
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: getString(context, R.string.unknown_error))
            }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val data: ApiResponse<LoginResponse>) : LoginState()
        data class Error(val message: String) : LoginState()
    }

}