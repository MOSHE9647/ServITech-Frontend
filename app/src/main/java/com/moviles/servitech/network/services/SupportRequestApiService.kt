package com.moviles.servitech.network.services

import com.moviles.servitech.network.requests.supportRequest.CreateSupportRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.model.SupportRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SupportRequestApiService {
    @POST("support-request")
    suspend fun createSupportRequest(
        @Body request: CreateSupportRequest
    ): ApiResponse<SupportRequest>
}