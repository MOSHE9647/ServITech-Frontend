package com.moviles.servitech.network.responses

data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: Map<String, String> = emptyMap()
)