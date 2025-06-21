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

/**
 * Validates a string input based on minimum length and empty checks.
 *
 * @param value The string value to validate.
 * @param minLength The minimum length required for the string.
 * @param emptyErrorMsg The error message to return if the string is empty.
 * @param shortErrorMsg The error message to return if the string is too short.
 * @return A [ValidationResult] indicating whether the validation passed or failed,
 * with appropriate error messages.
 */
fun validateString(
    value: String,
    minLength: Int,
    emptyErrorMsg: String,
    shortErrorMsg: String
): ValidationResult {
    return when {
        value.isBlank() -> invalid(emptyErrorMsg)
        value.length < minLength -> invalid(shortErrorMsg)
        else -> valid()
    }
}

/**
 * Indicates that the validation failed with an error message.
 *
 * @param errorMessage The error message to return.
 * @return A [ValidationResult] indicating the validation failed.
 */
fun invalid(errorMessage: String): ValidationResult =
    ValidationResult(false, errorMessage)

/**
 * Indicates that the validation passed successfully.
 *
 * @return A [ValidationResult] indicating the validation passed.
 */
fun valid(): ValidationResult = ValidationResult(true)