package com.moviles.servitech.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.moviles.servitech.model.CreateArticleRequest
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.network.services.ArticleApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    @ApplicationContext private val context: Context,
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

    /**
     * Creates a new article using the provided request and optional image URI.
     * @param request The details of the article to create.
     * @param imageUri Optional URI of an image to upload with the article.
     * @return True if the article was created successfully, false otherwise.
     */
    suspend fun create(request: CreateArticleRequest, imageUri: Uri?): Boolean {
        return try {
            Log.d("ArticleRepository", "Llamando al endpoint con: $request y $imageUri")

            val name = request.name.toRequestBody()
            val description = request.description.toRequestBody()
            val price = request.price.toString().toRequestBody()
            val categoryId = request.category_id.toString().toRequestBody()
            val subcategoryId = request.subcategory_id.toString().toRequestBody()

            val imagePart = imageUri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                inputStream?.use { input -> tempFile.outputStream().use { input.copyTo(it) } }

                val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", tempFile.name, reqFile)
            }

            val response = service.createArticle(
                name, description, price, categoryId, subcategoryId, imagePart
            )

            Log.d("ArticleRepository", "Código de respuesta: ${response.code()}")

            if (!response.isSuccessful) {
                Log.e("ArticleRepository", "ErrorBody: ${response.errorBody()?.string()}")
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ArticleRepository", "Error creando artículo", e)
            false
        }
    }
    // Fetches an article by its ID.
    suspend fun fetchById(id: Int): ArticleDto {
        val response = service.getArticleById(id)
        return if (response.isSuccessful) {
            response.body()?.data?.article ?: throw Exception("Artículo vacío")
        } else {
            val error = response.errorBody()?.string()
            Log.e("ArticleRepository", "Error al obtener artículo: $error")
            throw Exception("Error HTTP ${response.code()}")
        }
    }
    // deletes an article by its ID.
    suspend fun deleteById(id: Int): Boolean {
        return try {
            val response = service.deleteArticle(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ArticleRepository", "Error al eliminar artículo: ${e.message}")
            false
        }
    }

    // Updates an existing article with the given ID and request.
    suspend fun update(id: Int, request: CreateArticleRequest): Boolean {
        return try {
            val response = service.updateArticle(id, request)
            Log.d("ArticleRepository", "Update status: ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ArticleRepository", "Error al actualizar artículo", e)
            false
        }
    }




    // Helper extension
    private fun String.toRequestBody(): RequestBody =
        toRequestBody("text/plain".toMediaTypeOrNull())
}