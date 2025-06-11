package com.moviles.servitech.repositories

import com.moviles.servitech.network.services.SubcategoryApi
import javax.inject.Inject

class SubcategoryRepository @Inject constructor(
    private val api: SubcategoryApi
) {
    suspend fun fetchAll() = api.getAll().data?.subcategories ?: emptyList()

}