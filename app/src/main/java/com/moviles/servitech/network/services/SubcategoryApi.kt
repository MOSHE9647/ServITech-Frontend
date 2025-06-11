package com.moviles.servitech.network.services

import com.moviles.servitech.network.responses.subcategory.SubcategoryWrapper

import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.subcategory.SubcategoryDto
import retrofit2.http.GET

interface SubcategoryApi {
    @GET("api/v1/subcategories")
    suspend fun getAll(): ApiResponse<SubcategoryWrapper>
}
