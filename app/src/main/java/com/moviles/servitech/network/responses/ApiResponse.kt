package com.moviles.servitech.network.responses

/**
 * Generic API response class to standard
 * the structure of API responses.
 *
 * @param T The type of data contained in the response.
 * @param data The data returned by the API, can be null if no data is present.
 * @param status The HTTP status code of the response.
 * @param message A message providing additional information about the response.
 * @param errors A map of validation errors, where the key is the field name and
 * the value is a list of error messages.
 */
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?,
    val errors: Map<String, List<String>>? = null
)