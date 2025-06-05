package com.moviles.servitech.network.requests

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a request to register a new user.
 * Contains fields for the user's name, phone number, email,
 * password, and password confirmation.
 *
 * @property name The name of the user.
 * @property phone The phone number of the user.
 * @property email The email address of the user.
 * @property password The password chosen by the user.
 * @property passwordConfirmation The confirmation of the password.
 */
data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)
