package com.moviles.servitech.network.responses.subcategory

import com.moviles.servitech.network.responses.CategoryDto

data class SubcategoryDto(
    val id: Int,
    val category_id: Int,
    val name: String,
    val description: String,
    val category: CategoryDto
)
