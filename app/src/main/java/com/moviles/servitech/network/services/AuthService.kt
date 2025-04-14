package com.moviles.servitech.network.services

import com.moviles.servitech.network.requests.LoginRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/login")
    @Headers("Accept: */*")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
}