package com.moviles.servitech.network.requests.repairRequest

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class CreateRepairRequest(
    @SerializedName("customer_name") val customerName: RequestBody,
    @SerializedName("customer_phone") val customerPhone: RequestBody,
    @SerializedName("customer_email") val customerEmail: RequestBody,
    @SerializedName("article_name") val articleName: RequestBody,
    @SerializedName("article_type") val articleType: RequestBody,
    @SerializedName("article_brand") val articleBrand: RequestBody,
    @SerializedName("article_model") val articleModel: RequestBody,
    @SerializedName("article_serialnumber") val articleSerialNumber: RequestBody? = null,
    @SerializedName("article_accesories") val articleAccesories: RequestBody? = null,
    @SerializedName("article_problem") val articleProblem: RequestBody,
    @SerializedName("repair_status") val repairStatus: RequestBody,
    @SerializedName("repair_details") val repairDetails: RequestBody? = null,
    @SerializedName("repair_price") val repairPrice: RequestBody? = null,
    @SerializedName("received_at") val receivedAt: RequestBody,
    @SerializedName("repaired_at") val repairedAt: RequestBody? = null,
    val images: List<MultipartBody.Part>? = emptyList()
)
