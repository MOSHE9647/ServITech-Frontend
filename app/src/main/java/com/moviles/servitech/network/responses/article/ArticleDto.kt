package com.moviles.servitech.network.responses.article

import com.moviles.servitech.network.responses.CategoryDto
import com.moviles.servitech.network.responses.ImageDto
import com.moviles.servitech.network.responses.SubcategoryDto

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
