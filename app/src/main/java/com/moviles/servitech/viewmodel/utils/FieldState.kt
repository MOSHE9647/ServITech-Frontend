package com.moviles.servitech.viewmodel.utils

import androidx.lifecycle.MutableLiveData

/**
 * A generic class to represent the state of a field in a form or UI component.
 * It holds the data, error state, and an optional error message.
 *
 * @param T The type of data held by this field state.
 * @property data A [androidx.lifecycle.MutableLiveData] object that holds the data for the field.
 * @param error A [androidx.lifecycle.MutableLiveData] object that indicates whether there is an error in the field.
 * @property errorMessage A [androidx.lifecycle.MutableLiveData] object that holds an optional error message for the field.
 */
data class FieldState<T>(
    val data: MutableLiveData<T> = MutableLiveData(),
    val error: MutableLiveData<Boolean> = MutableLiveData(false),
    val errorMessage: MutableLiveData<String?> = MutableLiveData(null)
)