package com.moviles.servitech.network.services


import com.moviles.servitech.network.responses.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleService {
    @GET("api/v1/articles")
    suspend fun getArticles(): Response<ArticlesResponse>

    @GET("api/v1/articles/{category}")
    suspend fun getArticlesByCategory(
        @Path("category") category: String
    ): Response<ArticlesResponse>
}
