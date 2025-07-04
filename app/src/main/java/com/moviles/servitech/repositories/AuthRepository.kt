package com.moviles.servitech.repositories

import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.network.handlers.ApiHandler.handleApiCall
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.network.responses.auth.RegisterResponse
import com.moviles.servitech.network.services.AuthApiService
import com.moviles.servitech.repositories.helpers.DataSource
import com.moviles.servitech.repositories.helpers.Result
import javax.inject.Inject

/**
 * Sealed class representing the result of an authentication operation.
 *
 * It implements the [Result] interface and can either be a success with data of type T,
 * or an error with a message and optional field errors.
 *
 * @param T The type of data returned on success.
 */
sealed class AuthResult<out T> : Result<T> {
    /**
     * Represents a successful authentication operation.
     * Contains the authenticated data.
     *
     * @param T The type of data returned on success.
     * @property data The authenticated data.
     */
    data class Success<out T>(val data: T) : AuthResult<T>()

    /**
     * Represents an error that occurred during authentication.
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
            DataSource.Remote -> handleApiCall(
                remoteCall = { authApi.login(loginRequest) },
                onCallError = { msg, fields -> AuthResult.Error(msg, fields) },
                onRemoteSuccess = { AuthResult.Success(it) },
                logClass = className,
                transformTo = { it },
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )
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
            DataSource.Remote -> handleApiCall(
                remoteCall = { authApi.register(registerRequest) },
                onCallError = { msg, fields -> AuthResult.Error(msg, fields) },
                onRemoteSuccess = { AuthResult.Success(it) },
                logClass = className,
                transformTo = { it },
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )
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
            DataSource.Remote -> handleApiCall(
                remoteCall = { authApi.logout("Bearer $token") },
                onCallError = { msg, fields -> AuthResult.Error(msg, fields) },
                onRemoteSuccess = { AuthResult.Success(Unit) },
                logClass = className,
                transformTo = { it },
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )
            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

    /**
     * Resets the password for the user with the provided email.
     *
     * @param email The email address of the user requesting a password reset.
     * @param source The data source to use (Remote or Local).
     * @return An [AuthResult] containing either a successful operation or an error.
     */
    suspend fun resetPassword(email: String, source: DataSource): AuthResult<Unit> {
        return when (source) {
            DataSource.Remote -> handleApiCall(
                remoteCall = {
                    val emailMap = mapOf("email" to email)
                    authApi.resetPassword(emailMap)
                },
                onCallError = { msg, fields -> AuthResult.Error(msg, fields) },
                onRemoteSuccess = { AuthResult.Success(Unit) },
                logClass = className,
                transformTo = { it },
                errorMessage = stringProvider.getString(R.string.unknown_error)
            )

            DataSource.Local -> AuthResult.Error(stringProvider.getString(R.string.connection_error))
        }
    }

}