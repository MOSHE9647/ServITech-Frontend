package com.moviles.servitech.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.model.CreateArticleRequest
import com.moviles.servitech.repositories.ArticleRepository
import com.moviles.servitech.network.responses.article.ArticleDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repo: ArticleRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<List<ArticleDto>>(emptyList())
    val articles: StateFlow<List<ArticleDto>> = _articles

    private val _articleById = MutableStateFlow<ArticleDto?>(null)
    val articleById: StateFlow<ArticleDto?> = _articleById


    private val _deleteSuccess = mutableStateOf<Boolean?>(null)
    val deleteSuccess: State<Boolean?> = _deleteSuccess

    init {
        //  get all articles
        viewModelScope.launch {
            _articles.value = repo.fetchAll()
        }
    }

    //* get articles by category */
    fun loadByCategory(category: String) {
        viewModelScope.launch {
            val newList = repo.fetchByCategory(category)
            _articles.value = emptyList() // ← fuerza recarga de la lista
            _articles.value = newList
        }
    }

    // create article
    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    /**
     * Resets the create success state to false.
     * This is useful to clear the state after a successful creation
     * or when the dialog is dismissed.
     */
    fun resetCreateSuccess() {
        _createSuccess.value = false
    }

 /* Creates a new article with the given request and optional image URI.
     * Logs the request details and the result of the creation operation.
     *
     * @param request The request containing article details.
     * @param imageUri Optional URI of an image to associate with the article.
     */



    fun createArticle(request: CreateArticleRequest, imageUri: Uri?, categoryName: String) {
        viewModelScope.launch {
            val result = repo.create(request, imageUri)
            Log.d("ArticleViewModel", "Resultado: $result")
            if (result) {
                loadByCategory(categoryName) //  nombre correcto
                _createSuccess.value = true  // opcional, útil para LaunchedEffect
            }
        }
    }


    suspend fun loadArticleById(id: Int) {
        val article = repo.fetchById(id)
        _articleById.value = article
    }



/* Deletes an article by its ID and updates the delete success state.
     * @param id The ID of the article to delete.
     * @param onSuccess Callback to execute if the deletion is successful.
     */
fun deleteArticle(id: Int, categoryName: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
        val success = repo.deleteById(id)
        _deleteSuccess.value = success
        if (success) {
            loadByCategory(categoryName) // ← recarga la lista
            onSuccess()
        }
    }
}

    /*Updates an existing article with the given ID and request.
     * @param id The ID of the article to update.
     * @param request The request containing updated article details.
     * @param onSuccess Callback to execute if the update is successful.
     */
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    fun updateArticle(id: Int, request: CreateArticleRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repo.update(id, request)
            if (result) {
                _updateSuccess.value = true
                loadByCategory(request.category_id.toString())
                onSuccess()
            }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

}