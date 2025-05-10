package com.moviles.servitech.viewmodel

import androidx.lifecycle.MutableLiveData

data class FieldState<T>(
    val data: MutableLiveData<T> = MutableLiveData(),
    val error: MutableLiveData<Boolean> = MutableLiveData(false),
    val errorMessage: MutableLiveData<String?> = MutableLiveData(null)
)