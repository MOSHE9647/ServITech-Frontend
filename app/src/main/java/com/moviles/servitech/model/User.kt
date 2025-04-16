package com.moviles.servitech.model

data class User (
    val id: Int? = null,
    val role: String,
    val name: String,
    val email: String,
    val phone: String? = null
)