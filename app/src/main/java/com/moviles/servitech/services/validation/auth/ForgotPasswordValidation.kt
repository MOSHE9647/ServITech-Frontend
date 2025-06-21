package com.moviles.servitech.services.validation.auth

import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.services.validation.ValidationResult
import com.moviles.servitech.services.validation.invalid
import com.moviles.servitech.services.validation.valid
import javax.inject.Inject

class ForgotPasswordValidation @Inject constructor(
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
}