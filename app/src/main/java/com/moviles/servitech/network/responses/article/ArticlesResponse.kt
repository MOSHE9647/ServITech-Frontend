package com.moviles.servitech.network.responses.article

data class ArticlesResponse(
    val status: Int,
    val message: String,
    val data: ArticlesData
)