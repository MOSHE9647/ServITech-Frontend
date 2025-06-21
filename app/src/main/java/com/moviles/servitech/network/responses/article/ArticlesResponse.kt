package com.moviles.servitech.network.responses.article

data class ArticlesResponse(
    val status: Int,
    val message: String,
    val data: ArticlesData
)

// Data class representing the data structure of articles - one article
data class ArticleByIdResponse(
    val status: Int,
    val message: String,
    val data: ArticleByIdData
)

data class ArticleByIdData(
    val article: ArticleDto
)