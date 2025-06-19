package com.moviles.servitech.model

data class SupportRequest(
    val id: Int? = null,
    val userId: Int? = null,
    val date: String,
    val location: String,
    val detail: String
)