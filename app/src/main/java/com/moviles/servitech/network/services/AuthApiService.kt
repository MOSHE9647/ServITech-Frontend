package com.moviles.servitech.network.services

import com.moviles.servitech.common.Constants.API_AUTH_ROUTE
import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.requests.RegisterRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.auth.LoginResponse
import com.moviles.servitech.network.responses.auth.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/** TODO: Add a more explicative description for this file.
 * Service interface for authentication-related API calls.
 * Defines methods for user login, registration, and logout.
 * Uses Retrofit annotations to specify HTTP methods and endpoints.
 */
interface AuthApiService {
    /**
     * Logs in a user with the provided credentials.
     *
     * @param request The login request containing user credentials.
     * @return A [Response] containing an [ApiResponse] with [LoginResponse] data.
     */
    @POST("$API_AUTH_ROUTE/login")
    @Headers("Accept: */*")
    suspend fun login(@Body request: LoginRequest): Response< ApiResponse<LoginResponse> >

    /**
     * Registers a new user with the provided details.
     *
     * @param request The registration request containing user details.
     * @return A [Response] containing an [ApiResponse] with [RegisterResponse] data.
     */
    @POST("$API_AUTH_ROUTE/register")
    @Headers("Accept: */*")
    suspend fun register(@Body request: RegisterRequest): Response< ApiResponse<RegisterResponse> >

    /**
     * Logs out the user by invalidating their session.
     *
     * @param token The authorization token of the user to be logged out.
     * @return A [Response] containing an [ApiResponse] with no content.
     */
    @POST("$API_AUTH_ROUTE/logout")
    @Headers("Accept: */*")
    suspend fun logout(@Header("Authorization") token: String): Response< ApiResponse<Unit> >
}