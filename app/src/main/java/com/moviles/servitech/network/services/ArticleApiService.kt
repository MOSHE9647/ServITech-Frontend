package com.moviles.servitech.network.services

import com.moviles.servitech.common.Constants
import com.moviles.servitech.common.Constants.HEADER_ACCEPT_JSON
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.article.ArticleByIdResponse
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.network.responses.article.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part

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
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun getArticles(): Response<ArticlesResponse>

    /**
     * Fetches a list of articles filtered by category from the API.
     * @param category The category to filter articles by.
     * @return A [Response] containing an [ArticlesResponse] object
     * with the list of articles in the specified category.
     */
    @GET("${Constants.API_ARTICLES_ROUTE}/{category}")
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun getArticlesByCategory(
        @Path("category") category: String
    ): Response<ArticlesResponse>


  /**
     * Creates a new article with the provided details.
     * @param name The name of the article.
     * @param description The description of the article.
     * @param price The price of the article.
     * @param categoryId The ID of the category to which the article belongs.
     * @param subcategoryId The ID of the subcategory to which the article belongs.
     * @param image Optional image file for the article.
     * @return A [Response] indicating success or failure of the operation.
     */
    @Multipart
    @POST(Constants.API_ARTICLES_ROUTE)
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun createArticle(
        @Header("Authorization") authToken: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("subcategory_id") subcategoryId: RequestBody,
        @Part images: List<MultipartBody.Part>
  ): Response<Unit>

    /**
     * Fetches a specific article by its ID.
     * @param id The ID of the article to fetch.
     * @return A [Response] containing an [ArticlesResponse] object
     * with the details of the specified article.
     */
    @GET("${Constants.API_ARTICLES_ROUTE}/id/{id}")
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun getArticleById(
        @Path("id") id: Int
    ): Response<ArticleByIdResponse>

    /**
     * Deletes an article by its ID.
     * @param id The ID of the article to delete.
     * @return A [Response] indicating success or failure of the deletion operation.
     */

    @DELETE("${Constants.API_ARTICLES_ROUTE}/{id}")
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun deleteArticle(@Header("Authorization") authToken: String,
                              @Path("id") id: Int): Response<Void>

    /**
     * Updates an existing article with the provided details.
     * @param id The ID of the article to update.
     * @param request The request containing updated article details.
     * @return A [Response] containing an [ApiResponse] with the updated article.
     */
    @Multipart
    @POST("${Constants.API_ARTICLES_ROUTE}/{id}")
    @Headers(HEADER_ACCEPT_JSON)
    suspend fun updateArticle(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("subcategory_id") subcategoryId: RequestBody,
        @Part images: List<MultipartBody.Part>, // puede estar vacío o tener 1 imagen
        @Part("_method") method: RequestBody

    ): Response<ApiResponse<ArticleDto>>

}
