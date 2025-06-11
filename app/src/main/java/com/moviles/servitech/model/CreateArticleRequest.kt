package com.moviles.servitech.model

data class CreateArticleRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category_id: Int,
    val subcategory_id: Int,
    val images: List<ImageRequest>
)

data class ImageRequest(
    val path: String
)
