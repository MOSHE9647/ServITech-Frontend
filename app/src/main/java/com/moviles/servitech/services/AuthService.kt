package com.moviles.servitech.services

import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.network.responses.auth.RegisterResponse
import com.moviles.servitech.repositories.AuthRepository
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.repositories.helpers.DataSource
import javax.inject.Inject

/**
 * Service class for handling authentication operations.
 *
 * This class provides methods for user login, registration, and logout,
 * utilizing the [AuthRepository] to interact with remote or local data sources
 * based on network connectivity.
 *
 * @property networkStatusTracker Tracks the network connectivity status.
 * @property stringProvider Provides string resources for error messages.
 * @property authRepo The repository for authentication operations.
 */
class AuthService @Inject constructor(
    private val networkStatusTracker: NetworkStatusTracker,
    private val stringProvider: AndroidStringProvider,
    private val authRepo: AuthRepository
) {

    /**
     * Logins a user with the provided [LoginRequest].
     * This method checks the network connectivity status and
     * calls the appropriate repository method to perform the login operation.
     *
     * @param loginRequest The request object containing login credentials.
     * @return An [AuthResult] containing the login response or an error.
     */
    suspend fun login(loginRequest: LoginRequest): AuthResult<LoginResponse> {
        requireNotNull(loginRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "loginRequest")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.login(loginRequest, DataSource.Remote)
            false -> authRepo.login(loginRequest, DataSource.Local)
        }
    }

    /**
     * Registers a new user with the provided [RegisterRequest].
     * This method checks the network connectivity status and
     * calls the appropriate repository method to perform the registration operation.
     *
     * @param registerRequest The request object containing registration details.
     * @return An [AuthResult] containing the registration response or an error.
     */
    suspend fun register(registerRequest: RegisterRequest): AuthResult<RegisterResponse> {
        requireNotNull(registerRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "registerRequest")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.register(registerRequest, DataSource.Remote)
            false -> authRepo.register(registerRequest, DataSource.Local)
        }
    }

    /**
     * Logs out the user with the provided token.
     * This method checks the network connectivity status and
     * calls the appropriate repository method to perform the logout operation.
     *
     * @param token The authentication token of the user.
     * @return An [AuthResult] indicating the success or failure of the logout operation.
     */
    suspend fun logout(token: String): AuthResult<Unit> {
        requireNotNull(token) {
            stringProvider.getString(R.string.error_null_parameter_msg, "token")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.logout(token, DataSource.Remote)
            false -> authRepo.logout(token, DataSource.Local)
        }
    }

    /**
     * Resets the password for the user with the provided email.
     * This method checks the network connectivity status and
     * calls the appropriate repository method to perform the password reset operation.
     *
     * @param email The email address of the user requesting a password reset.
     * @return An [AuthResult] indicating the success or failure of the password reset operation.
     */
    suspend fun resetPassword(email: String): AuthResult<Unit> {
        requireNotNull(email) {
            stringProvider.getString(R.string.error_null_parameter_msg, "email")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.resetPassword(email, DataSource.Remote)
            false -> authRepo.resetPassword(email, DataSource.Local)
        }
    }

}