package com.moviles.servitech.repositories

import android.util.Log
import com.google.gson.Gson
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.network.responses.auth.RegisterResponse
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.services.AuthApiService
import javax.inject.Inject

/**
 * Sealed class representing the status of an authentication operation.
 * It can either be a success with data of type T,
 * or an error with a message and optional field errors.
 *
 * @param T The type of data returned on success.
 */
sealed class AuthResult<out T> {
    /**
     * Represents a successful authentication operation.
     * Contains the data returned from the operation.
     *
     * @param T The type of data returned on success.
     * @property data The data returned from the authentication operation.
     */
    data class Success<out T>(val data: T) : AuthResult<T>()

    /**
     * Represents an error that occurred during the authentication operation.
     * Contains an error message and optional field errors.
     *
     * @property message The error message describing the issue.
     * @property fieldErrors A map of field names to error messages, if applicable.
     */
    data class Error(
        val message: String,
        val fieldErrors: Map<String, String> = emptyMap()
    ) : AuthResult<Nothing>()
}

/**
 * Repository for handling authentication operations.
 * It provides methods for login, registration, and logout,
 * and handles both remote and local data sources.
 *
 * @property authApi The API service for authentication operations.
 * @property stringProvider Provides string resources for error messages.
 */
class AuthRepository @Inject constructor(
    private val authApi: AuthApiService,
    private val stringProvider: AndroidStringProvider
) {

    // The name of the class for logging purposes
    private val className = this::class.java.simpleName

    /**
     * Performs a login operation.
     *
     * @param loginRequest The request object containing login credentials.
     * @param source The data source to use (Remote or Local).
     * @return An [AuthResult] containing either a successful LoginResponse or an error.
     */
    suspend fun login(loginRequest: LoginRequest, source: DataSource): AuthResult<LoginResponse> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.login(loginRequest) }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    /**
     * Performs a registration operation.
     *
     * @param registerRequest The request object containing registration details.
     * @param source The data source to use (Remote or Local).
     * @return An [AuthResult] containing either a successful RegisterResponse or an error.
     */
    suspend fun register(registerRequest: RegisterRequest, source: DataSource): AuthResult<RegisterResponse> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.register(registerRequest) }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    /**
     * Performs a logout operation.
     *
     * @param token The authentication token of the user.
     * @param source The data source to use (Remote or Local).
     * @return An [AuthResult] containing either a successful logout or an error.
     */
    suspend fun logout(token: String, source: DataSource): AuthResult<Unit> {
        return when (source) {
            DataSource.Remote -> handleApiCall { authApi.logout("Bearer $token") }
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    /**
     * Handles the API call and processes the response.
     * If the response is successful, it returns the data.
     * If the response is an error, it parses the error body
     * and returns an AuthResult.Error.
     *
     * @param T The type of data returned on success.
     * @param apiCall The API call to execute.
     * @return An [AuthResult] containing either a successful response or an error.
     */
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

    /**
     * Parses the error body from the API response and returns an AuthResult.Error.
     * If the error body is null or empty, it returns a generic error message.
     *
     * @param errorBody The error body string from the API response.
     * @return An [AuthResult.Error] containing the parsed error message and field errors.
     */
    private fun parseError(errorBody: String?): AuthResult<Nothing> {
        return if (!errorBody.isNullOrEmpty()) {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            AuthResult.Error(errorResponse.message, errorResponse.errors)
        } else {
            AuthResult.Error(stringProvider.getString(R.string.unknown_error))
        }
    }

}