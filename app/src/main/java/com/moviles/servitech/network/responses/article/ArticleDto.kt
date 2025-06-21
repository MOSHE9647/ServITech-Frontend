package com.moviles.servitech.network.responses.article

import com.moviles.servitech.network.responses.CategoryDto
//import com.moviles.servitech.network.responses.ImageDto
import com.moviles.servitech.network.responses.subcategory.SubcategoryDto

data class ArticleDto(
    val id: Int,
    val category_id: Int,
    val subcategory_id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: CategoryDto,
    val subcategory: SubcategoryDto,
    val images: List<ImageDto>
)
data class ImageDto(
    val title: String,
    val path: String,
    val alt: String


)

val ImageDto.fixedUrl: String
    get() = path.replace("localhost", "10.0.2.2")