package com.moviles.servitech.network.services


import com.moviles.servitech.common.Constants
import com.moviles.servitech.network.responses.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleService {
    @GET(Constants.API_ARTICLES_ROUTE)
    suspend fun getArticles(): Response<ArticlesResponse>

    @GET("${Constants.API_ARTICLES_ROUTE}/{category}")
    suspend fun getArticlesByCategory(
        @Path("category") category: String
    ): Response<ArticlesResponse>
}
