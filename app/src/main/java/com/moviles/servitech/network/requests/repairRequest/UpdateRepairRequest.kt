package com.moviles.servitech.network.requests.repairRequest

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody

data class UpdateRepairRequest(
    @SerializedName("article_serialnumber") val articleSerialNumber: RequestBody? = null,
    @SerializedName("article_accesories") val articleAccesories: RequestBody? = null,
    @SerializedName("repair_status") val repairStatus: RequestBody,
    @SerializedName("repair_details") val repairDetails: RequestBody? = null,
    @SerializedName("repair_price") val repairPrice: RequestBody? = null,
    @SerializedName("repaired_at") val repairedAt: RequestBody? = null
)
