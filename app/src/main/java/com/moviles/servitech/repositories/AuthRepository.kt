package com.moviles.servitech.repositories

import android.util.Log
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.network.responses.auth.RegisterResponse
import com.moviles.servitech.core.providers.StringProvider
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.services.AuthApiService
import javax.inject.Inject

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap()
    ) : AuthResult<Nothing>()
}

class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val stringProvider: StringProvider
) {

    private val className = this::class.java.simpleName

    suspend fun login(loginRequest: LoginRequest, source: DataSource): AuthResult<LoginResponse> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.login(loginRequest) }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    suspend fun register(registerRequest: RegisterRequest, source: DataSource): AuthResult<RegisterResponse> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.register(registerRequest) }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    suspend fun logout(token: String, source: DataSource): AuthResult<Unit> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.logout("Bearer $token") }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    private inline fun <T> handleApiCall(apiCall: () -> retrofit2.Response<ApiResponse<T>>): AuthResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    return AuthResult.Success(it)
                } ?: return AuthResult.Error(
                    response.body()?.message ?: stringProvider.getString(R.string.unknown_error),
                    emptyMap()
                )
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Log.e(className, "Error: ${e.message}")
            AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    private fun parseError(errorBody: String?): AuthResult<Nothing> {
        return if (!errorBody.isNullOrEmpty()) {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            AuthResult.Error(errorResponse.message, errorResponse.errors)
        } else {
            AuthResult.Error(stringProvider.getString(R.string.unknown_error))
        }
    }

}