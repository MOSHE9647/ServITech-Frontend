package com.moviles.servitech.network.services

import com.moviles.servitech.common.Constants.API_REPAIR_REQUESTS_ROUTE
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.repairRequest.RepairRequestResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Service interface for handling repair-request related API calls.
 */
interface RepairRequestApiService {

    @GET(API_REPAIR_REQUESTS_ROUTE)
    @Headers("Accept: */*")
    suspend fun getAllRepairRequests(
        @Header("Authorization") authToken: String
    ): Response<ApiResponse<List<RepairRequestResponse>>>

    @GET("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun getRepairRequestByReceiptNumber(
        @Header("Authorization") authToken: String,
        @Path("receiptNumber") receiptNumber: String
    ): Response<ApiResponse<RepairRequestResponse>>

    @Multipart
    @POST(API_REPAIR_REQUESTS_ROUTE)
    @Headers("Accept: */*")
    suspend fun createRepairRequest(
        @Header("Authorization") authToken: String,
        @Part("customer_name") customerName: RequestBody,
        @Part("customer_phone") customerPhone: RequestBody,
        @Part("customer_email") customerEmail: RequestBody,
        @Part("article_name") articleName: RequestBody,
        @Part("article_type") articleType: RequestBody,
        @Part("article_brand") articleBrand: RequestBody,
        @Part("article_model") articleModel: RequestBody,
        @Part("article_serialnumber") articleSerialNumber: RequestBody?,
        @Part("article_accesories") articleAccesories: RequestBody?,
        @Part("article_problem") articleProblem: RequestBody,
        @Part("repair_status") repairStatus: RequestBody,
        @Part("repair_details") repairDetails: RequestBody?,
        @Part("repair_price") repairPrice: RequestBody?,
        @Part("received_at") receivedAt: RequestBody,
        @Part("repaired_at") repairedAt: RequestBody?,
        @Part images: List<MultipartBody.Part>? = emptyList<MultipartBody.Part>()
    ): Response<ApiResponse<RepairRequestResponse>>

    @Multipart
    @PUT("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun updateRepairRequest(
        @Header("Authorization") authToken: String,
        @Path("receiptNumber") receiptNumber: String,
        @Part("article_serialnumber") articleSerialNumber: RequestBody? = null,
        @Part("article_accesories") articleAccesories: RequestBody? = null,
        @Part("repair_status") repairStatus: RequestBody,
        @Part("repair_details") repairDetails: RequestBody? = null,
        @Part("repair_price") repairPrice: RequestBody? = null,
        @Part("repaired_at") repairedAt: RequestBody? = null
    ): Response<ApiResponse<RepairRequestResponse>>

    @DELETE("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun deleteRepairRequest(
        @Header("Authorization") authToken: String,
        @Path("receiptNumber") receiptNumber: String
    ): Response<ApiResponse<Unit>>

}