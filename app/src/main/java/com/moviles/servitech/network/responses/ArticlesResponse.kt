package com.moviles.servitech.network.responses

data class ArticlesResponse(
    val status: Int,
    val message: String,
    val data: ArticlesData
)
