package com.moviles.servitech.services.validation.auth

import com.moviles.servitech.R
import com.moviles.servitech.common.PhoneUtils
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.services.validation.ValidationResult
import com.moviles.servitech.services.validation.invalid
import com.moviles.servitech.services.validation.valid
import javax.inject.Inject

/**
 * Validation class for register inputs.
 *
 * This class provides methods to validate user inputs during registration,
 * including name, phone, email, password, and password confirmation.
 *
 * Each validation method returns a [ValidationResult] indicating
 * whether the input is valid or not, along with an error message if applicable.
 *
 * @property stringProvider Provides string resources for error messages.
 */
class RegisterValidation @Inject constructor(
    private val stringProvider: AndroidStringProvider
) {

    /**
     * Validates the user's name.
     *
     * @param name The name to validate.
     * @return A [ValidationResult] indicating whether the name is valid or not.
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> invalid(stringProvider.getString(R.string.name_empty_error))
            name.length < 3 -> invalid(stringProvider.getString(R.string.name_length_error))
            else -> valid()
        }
    }

    /**
     * Validates the user's phone number.
     *
     * @param phone The phone number to validate.
     * @return A [ValidationResult] indicating whether the phone number is valid or not.
     */
    fun validatePhone(phone: String): ValidationResult {
        // Normalize and format the phone number for validation
        val normalized = PhoneUtils.normalizePhoneInput(phone)
        val formatted = PhoneUtils.formatPhoneForDisplay(normalized)

        // Check for empty input
        if (formatted.isEmpty()) {
            return invalid(stringProvider.getString(R.string.phone_empty_error))
        }

        // Validate length and format
        val isCostaRica = normalized.startsWith("+506")
        val expectedLength = if (isCostaRica) 12 else 8

        if (normalized.length > expectedLength) {
            return invalid(stringProvider.getString(R.string.phone_length_error))
        }

        if (formatted.length < 14) {
            return invalid(stringProvider.getString(R.string.phone_length_error))
        }

        // Validate pattern
        if (!formatted.matches(Regex("^\\+[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$"))) {
            return invalid(stringProvider.getString(R.string.phone_invalid_error))
        }

        return valid()
    }

    /**
     * Validates the user's email address.
     *
     * @param email The email address to validate.
     * @return A [ValidationResult] indicating whether the email is valid or not.
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

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> invalid(stringProvider.getString(R.string.password_empty_error))
            password.length <= 8 -> invalid(stringProvider.getString(R.string.password_length_error))
            else -> valid()
        }
    }

    fun validatePasswordConfirmation(
        password: String,
        confirmation: String
    ): ValidationResult {
        return when {
            confirmation.isEmpty() -> invalid(stringProvider.getString(R.string.confirm_password_empty_error))
            password != confirmation -> invalid(stringProvider.getString(R.string.confirm_password_not_match))
            else -> valid()
        }
    }

}