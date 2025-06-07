package com.moviles.servitech.network.responses.repairRequest

import com.google.gson.annotations.SerializedName
import com.moviles.servitech.network.responses.ImageResponse

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