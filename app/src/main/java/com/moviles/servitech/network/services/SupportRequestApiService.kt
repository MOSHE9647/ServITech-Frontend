package com.moviles.servitech.network.services

import com.moviles.servitech.network.requests.supportRequest.CreateSupportRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.model.SupportRequest
import com.moviles.servitech.common.Constants
import com.moviles.servitech.common.Constants.HEADER_ACCEPT_JSON
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Header

interface SupportRequestApiService {
    @POST("api/v1/es/support-request")
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun createSupportRequest(
        @Header("Authorization") authToken: String,
        @Body request: CreateSupportRequest
    ): ApiResponse<SupportRequest>
}