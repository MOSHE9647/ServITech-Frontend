package com.moviles.servitech.network.requests.repairRequest

import com.google.gson.annotations.SerializedName

data class UpdateRepairRequest(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("receipt_number") val receiptNumber: String,
    @SerializedName("article_serialnumber") val articleSerialNumber: String? = null,
    @SerializedName("article_accesories") val articleAccesories: String? = null,
    @SerializedName("repair_status") val repairStatus: String,
    @SerializedName("repair_details") val repairDetails: String? = null,
    @SerializedName("repair_price") val repairPrice: Double? = null,
    @SerializedName("repaired_at") val repairedAt: String? = null
)
