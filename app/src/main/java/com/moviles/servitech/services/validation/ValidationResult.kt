package com.moviles.servitech.services.validation

/**
 * Represents the result of a validation check.
 *
 * @property isValid Indicates whether the validation passed.
 * @property errorMessage An optional error message if the validation failed.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)