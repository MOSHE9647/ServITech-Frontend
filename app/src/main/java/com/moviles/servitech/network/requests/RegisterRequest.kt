package com.moviles.servitech.network.requests

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)
