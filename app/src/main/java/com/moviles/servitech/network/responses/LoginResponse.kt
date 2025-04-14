package com.moviles.servitech.network.responses

import com.moviles.servitech.model.User

data class LoginResponse(
    val user: User,
    val token: String,
    val expiresIn: Int
)