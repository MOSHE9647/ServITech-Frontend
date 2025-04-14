package com.moviles.servitech.network.repository

import android.util.Patterns
import com.moviles.servitech.R
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.network.responses.LoginResponse
import com.moviles.servitech.network.services.AuthService
import com.moviles.servitech.network.services.providers.StringProvider
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    fun validateEmail(email: String): ValidationResult
    fun validatePassword(password: String): ValidationResult
}

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val stringProvider: StringProvider
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val loginRequest = LoginRequest(email, password)
            val response = authService.login(loginRequest)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                if (apiResponse.data != null) {
                    Result.success(LoginResult.Success(apiResponse.data))
                } else {
                    Result.success(LoginResult.Error(apiResponse.message, emptyMap()))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.success(LoginResult.Error(
                        errorResponse.message,
                        errorResponse.errors
                    ))
                } else {
                    Result.failure(Exception(stringProvider.getString(R.string.unknown_error)))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage))
        }
    }

    override fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.email_empty_error)
            )
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                false,
                stringProvider.getString(R.string.email_invalid_error)
            )
            else -> ValidationResult(true)
        }
    }

    override fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.password_empty_error)
            )
            password.length <= 8 -> ValidationResult(
                false,
                stringProvider.getString(R.string.password_length_error)
            )
            else -> ValidationResult(true)
        }
    }
}

sealed class LoginResult {
    data class Success(val data: LoginResponse) : LoginResult()
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap()
    ) : LoginResult()
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)