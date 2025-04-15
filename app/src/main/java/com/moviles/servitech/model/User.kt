package com.moviles.servitech.model

import com.google.gson.annotations.SerializedName

data class User (
    val id: Int? = null,
    val role: String,
    val name: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val password: String? = null,
)