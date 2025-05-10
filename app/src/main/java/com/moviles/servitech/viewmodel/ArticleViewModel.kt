package com.moviles.servitech.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.network.repositories.ArticleRepository
import com.moviles.servitech.network.responses.article.ArticleDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repo: ArticleRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<List<ArticleDto>>(emptyList())
    val articles: StateFlow<List<ArticleDto>> = _articles

    init {
        // carga inicial (todos los artículos)
        viewModelScope.launch {
            _articles.value = repo.fetchAll()
        }
    }

    /** Nuevo: carga sólo los artículos de la categoría indicada */
    fun loadByCategory(category: String) {
        viewModelScope.launch {
            _articles.value = repo.fetchByCategory(category)
        }
    }
}
