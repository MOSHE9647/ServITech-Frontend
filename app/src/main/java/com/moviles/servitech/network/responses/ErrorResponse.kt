package com.moviles.servitech.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: Map<String, String> = emptyMap()
)