package com.moviles.servitech.network.repositories

import com.moviles.servitech.network.responses.ArticleDto
import com.moviles.servitech.network.services.ArticleService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val service: ArticleService
) {
    /** get all articles */
    suspend fun fetchAll(): List<ArticleDto> {
        val resp = service.getArticles()
        return if (resp.isSuccessful) {
            resp.body()?.data?.articles.orEmpty()
        } else emptyList()
    }

    //** get articles by category */
    suspend fun fetchByCategory(category: String): List<ArticleDto> {
        val resp = service.getArticlesByCategory(category)
        return if (resp.isSuccessful) {
            resp.body()?.data?.articles.orEmpty()
        } else emptyList()
    }
}
