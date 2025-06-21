package com.moviles.servitech.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.servitech.network.responses.subcategory.SubcategoryDto
import com.moviles.servitech.repositories.SubcategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubcategoryViewModel @Inject constructor(
    private val repository: SubcategoryRepository
) : ViewModel() {

    private val _subcategories = MutableStateFlow<List<SubcategoryDto>>(emptyList())
    val subcategories: StateFlow<List<SubcategoryDto>> = _subcategories

    init {
        viewModelScope.launch {
            _subcategories.value = repository.fetchAll()
        }
    }
    fun getSubcategoriesByCategory(categoryName: String): List<SubcategoryDto> {
        return _subcategories.value.filter {
            it.category.name.equals(categoryName, ignoreCase = true)
        }
    }

}
