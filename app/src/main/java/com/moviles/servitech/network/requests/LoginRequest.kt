package com.moviles.servitech.network.requests

/**
 * Data class representing a login request.
 * It contains the necessary fields for user authentication.
 *
 * @param email The email address of the user.
 * @param password The password of the user.
 */
data class LoginRequest(
    val email: String,
    val password: String
)