package com.moviles.servitech.services.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)