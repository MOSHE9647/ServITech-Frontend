package com.moviles.servitech.network.services

import com.moviles.servitech.common.Constants
import com.moviles.servitech.network.responses.article.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/** TODO: Add a more explicative description for this file.
 * Service interface for fetching articles from the API.
 * Defines methods to retrieve articles and articles by category.
 * Uses Retrofit for network calls.
 */
interface ArticleApiService {
    /**
     * Fetches a list of articles from the API.
     * @return A [Response] containing an [ArticlesResponse] object
     * with the list of articles.
     */
    @GET(Constants.API_ARTICLES_ROUTE)
    suspend fun getArticles(): Response<ArticlesResponse>

    /**
     * Fetches a list of articles filtered by category from the API.
     * @param category The category to filter articles by.
     * @return A [Response] containing an [ArticlesResponse] object
     * with the list of articles in the specified category.
     */
    @GET("${Constants.API_ARTICLES_ROUTE}/{category}")
    suspend fun getArticlesByCategory(
        @Path("category") category: String
    ): Response<ArticlesResponse>
}
