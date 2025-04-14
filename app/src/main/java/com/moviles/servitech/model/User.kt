package com.moviles.servitech.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id") val id: Int? = null,
    @SerializedName("role") val role: String,
    @SerializedName("name") val name: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("deleted_at") val deletedAt: String? = null,
)