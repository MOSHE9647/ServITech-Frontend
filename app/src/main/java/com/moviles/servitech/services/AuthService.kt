package com.moviles.servitech.services

import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.network.NetworkStatusTracker
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.LoginResponse
import com.moviles.servitech.network.responses.RegisterResponse
import com.moviles.servitech.repositories.AuthRepository
import com.moviles.servitech.repositories.AuthResult
import com.moviles.servitech.repositories.DataSource
import javax.inject.Inject

class AuthService @Inject constructor(
    private val networkStatusTracker: NetworkStatusTracker,
    private val stringProvider: AndroidStringProvider,
    private val authRepo: AuthRepository
) {

    suspend fun login(loginRequest: LoginRequest): AuthResult<LoginResponse> {
        requireNotNull(loginRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "loginRequest")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.login(loginRequest, DataSource.Remote)
            false -> authRepo.login(loginRequest, DataSource.Local)
        }
    }

    suspend fun register(registerRequest: RegisterRequest): AuthResult<RegisterResponse> {
        requireNotNull(registerRequest) {
            stringProvider.getString(R.string.error_null_parameter_msg, "registerRequest")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.register(registerRequest, DataSource.Remote)
            false -> authRepo.register(registerRequest, DataSource.Local)
        }
    }

    suspend fun logout(token: String): AuthResult<Unit> {
        requireNotNull(token) {
            stringProvider.getString(R.string.error_null_parameter_msg, "token")
        }
        return when (networkStatusTracker.isConnected.value) {
            true -> authRepo.logout(token, DataSource.Remote)
            false -> authRepo.logout(token, DataSource.Local)
        }
    }

}