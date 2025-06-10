package com.moviles.servitech.viewmodel.utils

sealed class ViewModelState {
    object Loading : ViewModelState()
    data class Success<T>(val data: T? = null) : ViewModelState()
    data class Error(val message: String) : ViewModelState()
}
