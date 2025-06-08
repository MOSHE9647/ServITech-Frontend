package com.moviles.servitech.network.services

import com.moviles.servitech.common.Constants.API_REPAIR_REQUESTS_ROUTE
import com.moviles.servitech.network.requests.repairRequest.CreateRepairRequest
import com.moviles.servitech.network.requests.repairRequest.UpdateRepairRequest
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.repairRequest.RepairRequestResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
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
 *
 * This interface defines methods to interact with the repair requests API,
 * including fetching all requests, fetching a specific request by receipt number,
 * creating a new repair request, updating an existing request, and deleting a request.
 *
 * Each method corresponds to a specific HTTP request type and includes necessary headers
 * and parameters. The methods return a [Response] containing an [ApiResponse] with the
 * expected data type, such as a list of repair requests or a single repair request response.
 *
 * @see [ApiResponse] for the structure of the API response.
 * @see [RepairRequestResponse] for the structure of a repair request response.
 * @see [API_REPAIR_REQUESTS_ROUTE] for the base API endpoint.
 */
interface RepairRequestApiService {

    /**
     * Fetches all repair requests.
     *
     * @param authToken The authorization token for the request.
     * @return A [Response] containing an [ApiResponse] with a list of [RepairRequestResponse].
     */
    @GET(API_REPAIR_REQUESTS_ROUTE)
    @Headers("Accept: */*")
    suspend fun getAllRepairRequests(
        @Header("Authorization") authToken: String
    ): Response<ApiResponse<List<RepairRequestResponse>>>

    /**
     * Fetches a specific repair request by its receipt number.
     *
     * @param authToken The authorization token for the request.
     * @param receiptNumber The receipt number of the repair request to fetch.
     * @return A [Response] containing an [ApiResponse] with the [RepairRequestResponse].
     */
    @GET("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun getRepairRequestByReceiptNumber(
        @Header("Authorization") authToken: String,
        @Path("receiptNumber") receiptNumber: String
    ): Response<ApiResponse<RepairRequestResponse>>

    /**
     * Creates a new repair request using the data contained in [CreateRepairRequest]
     * and passed as multipart form data in function parameters.
     *
     * @param authToken The authorization token for the request.
     * @return A [Response] containing an [ApiResponse] with a [RepairRequestResponse].
     */
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

    /**
     * Updates an existing repair request identified by its receipt number.
     *
     */
    @Multipart
    @PUT("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun updateRepairRequest(
        @Header("Authorization") authToken: String,
        @Body request: UpdateRepairRequest
    ): Response<ApiResponse<RepairRequestResponse>>

    @DELETE("$API_REPAIR_REQUESTS_ROUTE/{receiptNumber}")
    @Headers("Accept: */*")
    suspend fun deleteRepairRequest(
        @Header("Authorization") authToken: String,
        @Path("receiptNumber") receiptNumber: String
    ): Response<ApiResponse<Unit>>

}