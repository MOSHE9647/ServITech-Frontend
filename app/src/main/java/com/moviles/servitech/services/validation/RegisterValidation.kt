package com.moviles.servitech.services.validation

import android.util.Patterns
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import javax.inject.Inject

class RegisterValidation @Inject constructor(
    private val stringProvider: AndroidStringProvider
) {

    fun validateName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.name_empty_error)
            )
            name.length < 3 -> ValidationResult(
                false,
                stringProvider.getString(R.string.name_length_error)
            )
            else -> ValidationResult(true)
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.phone_empty_error)
            )
            phone.length < 10 -> ValidationResult(
                false,
                stringProvider.getString(R.string.phone_length_error)
            )
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.email_empty_error)
            )
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                false,
                stringProvider.getString(R.string.email_invalid_error)
            )
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.password_empty_error)
            )
            password.length <= 8 -> ValidationResult(
                false,
                stringProvider.getString(R.string.password_length_error)
            )
            else -> ValidationResult(true)
        }
    }

    fun validatePasswordConfirmation(
        password: String,
        confirmation: String
    ): ValidationResult {
        return when {
            confirmation.isEmpty() -> ValidationResult(
                false,
                stringProvider.getString(R.string.confirm_password_empty_error)
            )
            password != confirmation -> ValidationResult(
                false,
                stringProvider.getString(R.string.confirm_password_not_match)
            )
            else -> ValidationResult(true)
        }
    }

}