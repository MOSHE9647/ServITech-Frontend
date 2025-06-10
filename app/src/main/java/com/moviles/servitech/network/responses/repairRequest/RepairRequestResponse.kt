package com.moviles.servitech.network.responses.repairRequest

import com.google.gson.annotations.SerializedName
import com.moviles.servitech.network.responses.ImageResponse

/**
 * Data class representing a repair request response from the API.
 * Contains details about the repair request, including customer information,
 * article details, repair status, and timestamps.
 *
 * @property receiptNumber The unique receipt number for the repair request.
 * @property customerName The name of the customer who submitted the request.
 * @property customerPhone The phone number of the customer.
 * @property customerEmail The email address of the customer.
 * @property articleName The name of the article being repaired.
 * @property articleType The type of the article (e.g., phone, laptop).
 * @property articleBrand The brand of the article.
 * @property articleModel The model of the article.
 * @property articleSerialNumber The serial number of the article (optional).
 * @property articleAccesories Accessories included with the article (optional).
 * @property articleProblem Description of the problem with the article.
 * @property repairStatus Current status of the repair (e.g., pending, completed).
 * @property repairDetails Additional details about the repair (optional).
 * @property repairPrice Price for the repair (optional).
 * @property receivedAt Timestamp when the request was received.
 * @property repairedAt Timestamp when the repair was completed (optional).
 * @property images List of images related to the repair request (optional).
 */
data class RepairRequestResponse(
    @SerializedName("receipt_number") val receiptNumber: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("customer_email") val customerEmail: String,
    @SerializedName("article_name") val articleName: String,
    @SerializedName("article_type") val articleType: String,
    @SerializedName("article_brand") val articleBrand: String,
    @SerializedName("article_model") val articleModel: String,
    @SerializedName("article_serialnumber") val articleSerialNumber: String? = null,
    @SerializedName("article_accesories") val articleAccesories: String? = null,
    @SerializedName("article_problem") val articleProblem: String,
    @SerializedName("repair_status") val repairStatus: String,
    @SerializedName("repair_details") val repairDetails: String? = null,
    @SerializedName("repair_price") val repairPrice: Double? = null,
    @SerializedName("received_at") val receivedAt: String,
    @SerializedName("repaired_at") val repairedAt: String? = null,
    val images: List<ImageResponse>? = emptyList()
)

/**
 * Data class representing the response for getting all repair requests.
 * Contains a list of repair request responses.
 *
 * @property repairRequests List of [RepairRequestResponse] containing details of all repair requests.
 */
data class GetAllRepairRequestsResponse(
    val repairRequests: List<RepairRequestResponse>
)

/**
 * Data class representing the response for getting a repair request by its receipt number.
 * Contains the details of the specific repair request.
 *
 * @property repairRequest The [RepairRequestResponse] containing details of the requested repair.
 */
data class GetRepairRequestByReceiptNumberResponse(
    val repairRequest: RepairRequestResponse
)

/**
 * Data class representing the response for creating a repair request.
 * Contains the created repair request details.
 *
 * @property repairRequest The created repair request details.
 */
data class CreateRepairRequestResponse(
    val repairRequest: RepairRequestResponse
)

/**
 * Data class representing the response for updating a repair request.
 * Contains the updated repair request details.
 *
 * @property repairRequest The updated repair request details.
 */
data class UpdateRepairRequestResponse(
    val repairRequest: RepairRequestResponse
)