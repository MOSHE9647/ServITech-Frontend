package com.moviles.servitech.network.responses

import com.google.gson.annotations.SerializedName
import com.moviles.servitech.model.User

data class LoginResponse(
    val user: User,
    val token: String,
    @SerializedName("expires_in")
    val expiresIn: Long
)