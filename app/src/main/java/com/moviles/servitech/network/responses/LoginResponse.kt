package com.moviles.servitech.network.responses

data class LoginResponse(
    val token: String,
    val expiresIn: Int
)