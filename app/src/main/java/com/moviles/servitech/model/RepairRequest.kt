package com.moviles.servitech.model

data class RepairRequest(
    val id: Int? = null,
    val receiptNumber: String? = null,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val articleName: String,
    val articleType: String,
    val articleBrand: String,
    val articleModel: String,
    val articleSerialNumber: String? = null,
    val articleAccesories: String? = null,
    val articleProblem: String,
    val repairStatus: String,
    val repairDetails: String? = null,
    val repairPrice: Double? = null,
    val receivedAt: String,
    val repairedAt: String? = null,
    val images: List<Image>? = emptyList()
)
