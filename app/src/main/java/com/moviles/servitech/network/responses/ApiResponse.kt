package com.moviles.servitech.network.responses

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?,
    val errors: Map<String, List<String>>? = null
)