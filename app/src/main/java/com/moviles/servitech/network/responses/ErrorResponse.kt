package com.moviles.servitech.network.responses

/**
 * Generic API response class to standardize the structure of API responses.
 * This class is used to encapsulate error responses from the API.
 *
 * @param status The HTTP status code of the response.
 * @param message A human-readable message describing the error.
 * @param errors A map containing specific error details, where
 * the key is the field name and the value is the error message.
 */
data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: Map<String, String> = emptyMap()
)