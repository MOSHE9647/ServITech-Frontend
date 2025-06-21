package com.moviles.servitech.services.validation.auth

import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.services.validation.ValidationResult
import com.moviles.servitech.services.validation.invalid
import com.moviles.servitech.services.validation.valid
import javax.inject.Inject

/**
 * Validation class for login inputs.
 *
 * This class provides methods to validate email and password inputs for user login.
 * It checks if the email is in a valid format and if the password meets the required criteria.
 */
class LoginValidation @Inject constructor(
    private val stringProvider: AndroidStringProvider
) {

    /**
     * Validates the email input.
     *
     * @param email The email string to validate.
     * @return A [ValidationResult] indicating whether the
     * email is valid or not, along with an error message if invalid.
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> invalid(stringProvider.getString(R.string.email_empty_error))
            !email.matches(
                Regex(pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|net|org|edu|gov|mil|info|io|co)$")
            ) -> invalid(stringProvider.getString(R.string.email_invalid_error))

            else -> valid()
        }
    }

    /**
     * Validates the password input.
     *
     * @param password The password string to validate.
     * @return A [ValidationResult] indicating whether the
     * password is valid or not, along with an error message if invalid.
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> invalid(stringProvider.getString(R.string.password_empty_error))
            password.length <= 8 -> invalid(stringProvider.getString(R.string.password_length_error))
            else -> valid()
        }
    }

}