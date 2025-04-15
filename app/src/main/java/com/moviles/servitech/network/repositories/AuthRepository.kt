package com.moviles.servitech.network.repositories

import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.network.responses.LoginResponse
import com.moviles.servitech.network.services.AuthService
import com.moviles.servitech.network.services.providers.StringProvider
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun logout(token: String): Result<LogoutResult>
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
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.success(LoginResult.Error(
                        errorResponse.message,
                        errorResponse.errors
                    ))
                } else {
                    Result.failure(Exception(stringProvider.getString(R.string.unknown_error)))
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error: ${e.message}")
            Result.failure(Exception(stringProvider.getString(R.string.connection_error)))
        }
    }

    override suspend fun logout(token: String): Result<LogoutResult> {
        return try {
            val response = authService.logout("Bearer $token")

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                if (apiResponse.status == 200) {
                    Result.success(LogoutResult.Success)
                } else {
                    Result.success(LogoutResult.Error(apiResponse.message))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.success(LogoutResult.Error(errorResponse.message))
                } else {
                    Result.failure(Exception(stringProvider.getString(R.string.unknown_error)))
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error: ${e.message}")
            Result.failure(Exception(stringProvider.getString(R.string.connection_error)))
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

sealed class LogoutResult {
    object Success : LogoutResult()
    data class Error(val message: String) : LogoutResult()
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)