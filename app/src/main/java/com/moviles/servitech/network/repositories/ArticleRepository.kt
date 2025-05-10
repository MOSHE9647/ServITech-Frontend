package com.moviles.servitech.network.repositories

import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.network.services.ArticleApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val service: ArticleApiService
) {
    /** Get all the articles */
    suspend fun fetchAll(): List<ArticleDto> {
        val resp = service.getArticles()
        return if (resp.isSuccessful) {
            resp.body()?.data?.articles.orEmpty()
        } else emptyList()
    }

    /** Gets only the articles by the indicated category */
    suspend fun fetchByCategory(category: String): List<ArticleDto> {
        val resp = service.getArticlesByCategory(category)
        return if (resp.isSuccessful) {
            resp.body()?.data?.articles.orEmpty()
        } else emptyList()
    }
}
