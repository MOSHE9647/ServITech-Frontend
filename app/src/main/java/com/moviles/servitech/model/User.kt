package com.moviles.servitech.model

/**
 * Data class representing a user in the application.
 * This class is used to store user information such as id, role, name, email, and phone.
 *
 * @param id Unique identifier for the user, nullable to allow for auto-generated IDs.
 * @param role The role of the user (e.g., admin, user).
 * @param name The name of the user.
 * @param email The email address of the user.
 * @param phone Optional phone number of the user, nullable to allow for users without a phone number.
 */
data class User (
    val id: Int? = null,
    val role: String,
    val name: String,
    val email: String,
    val phone: String? = null
)