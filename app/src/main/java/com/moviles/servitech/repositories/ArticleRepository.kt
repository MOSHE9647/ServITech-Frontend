package com.moviles.servitech.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.moviles.servitech.R
import com.moviles.servitech.core.session.SessionManager
import com.moviles.servitech.model.CreateArticleRequest
import com.moviles.servitech.model.enums.UserRole
import com.moviles.servitech.network.responses.article.ArticleDto
import com.moviles.servitech.network.services.ArticleApiService
import com.moviles.servitech.services.helpers.ServicesHelper.checkRoleOrError
import com.moviles.servitech.services.helpers.ServicesHelper.getAuthTokenOrError
import com.moviles.servitech.viewmodel.utils.FileHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
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
    private val service: ArticleApiService,
    private val sessionManager: SessionManager,
    private val fileHelper: FileHelper
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

            val imageParts = imageUri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                inputStream?.use { input -> tempFile.outputStream().use { input.copyTo(it) } }

                val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                listOf(MultipartBody.Part.createFormData("images[]", tempFile.name, reqFile))
            } ?: emptyList()


            val token = getAuthTokenOrError(sessionManager)
                ?: return error(R.string.error_authentication_required)
            val authToken = "Bearer $token"

            // Check if the user has admin role before proceeding
            if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
                return error(R.string.error_user_not_authorized_msg)
            }

            val response = service.createArticle(
                authToken, name, description, price, categoryId, subcategoryId, imageParts
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
        return try {
            val response = service.getArticleById(id)
            Log.d("ArticleRepository", "Response code: ${response.code()}")
            Log.d("ArticleRepository", "Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("ArticleRepository", "Response body received successfully")
                    responseBody.data?.article ?: throw Exception("Article data is null")
                } else {
                    Log.e("ArticleRepository", "Response body is null")
                    throw Exception("Empty response body")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ArticleRepository", "HTTP Error ${response.code()}: $errorBody")
                throw Exception("HTTP Error ${response.code()}: ${errorBody ?: "Unknown error"}")
            }
        } catch (e: Exception) {
            when (e) {
                is java.io.EOFException -> {
                    Log.e(
                        "ArticleRepository",
                        "JSON parsing error - incomplete response for article $id",
                        e
                    )
                    throw Exception("Server response is incomplete. Please try again.")
                }

                is com.google.gson.JsonSyntaxException -> {
                    Log.e("ArticleRepository", "JSON syntax error for article $id", e)
                    throw Exception("Invalid response format from server.")
                }

                else -> {
                    Log.e("ArticleRepository", "Error fetching article $id", e)
                    throw Exception("Failed to load article: ${e.message}")
                }
            }
        }
    }
    // deletes an article by its ID.

    suspend fun deleteById(id: Int): Boolean {
        return try {

            val token = getAuthTokenOrError(sessionManager)
                ?: return error(R.string.error_authentication_required)
            val authToken = "Bearer $token"

            // Check if the user has admin role before proceeding
            if (!checkRoleOrError(sessionManager, UserRole.ADMIN)) {
                return error(R.string.error_user_not_authorized_msg)
            }

            val response = service.deleteArticle(authToken, id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ArticleRepository", "Error al eliminar artículo: ${e.message}")
            false
        }
    }

    // Helper function to log and return an error message
    suspend fun updateWithImage(
        id: Int,
        request: CreateArticleRequest,
        imageUri: Uri?
    ): Boolean {
        return try {
            val token = getAuthTokenOrError(sessionManager) ?: return false
            val authToken = "Bearer $token"

            val name = request.name.toRequestBody("text/plain".toMediaType())
            val description = request.description.toRequestBody("text/plain".toMediaType())
            val price = request.price.toString().toRequestBody("text/plain".toMediaType())
            val categoryId =
                request.category_id.toString().toRequestBody("text/plain".toMediaType())
            val subcategoryId =
                request.subcategory_id.toString().toRequestBody("text/plain".toMediaType())
            val methodOverride = "PUT".toRequestBody("text/plain".toMediaType()) // 👈 AQUI

            val imageParts = mutableListOf<MultipartBody.Part>()
            imageUri?.let {
                val file = fileHelper.getFileFromUri(it)
                val requestFile = file.asRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part.createFormData("images[]", file.name, requestFile)
                imageParts.add(part)
            }

            //  Call ENDPOINT COMO POST CON override PUT
            val response = service.updateArticle(
                authToken,
                id,
                name,
                description,
                price,
                categoryId,
                subcategoryId,
                imageParts,
                methodOverride
            )

            Log.d("ArticleRepository", "Update with image status: ${response.code()}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ArticleRepository", "Error al actualizar con imagen", e)
            false
        }
    }

    // Helper extension
    private fun String.toRequestBody(): RequestBody =
        toRequestBody("text/plain".toMediaTypeOrNull())
}