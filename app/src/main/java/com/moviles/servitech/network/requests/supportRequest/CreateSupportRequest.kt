package com.moviles.servitech.network.requests.supportRequest

data class CreateSupportRequest(
    val date: String,
    val location: String,
    val detail: String
)