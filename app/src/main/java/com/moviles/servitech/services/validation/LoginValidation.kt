package com.moviles.servitech.services.validation

import android.util.Patterns
import com.moviles.servitech.R
import com.moviles.servitech.core.providers.AndroidStringProvider
import javax.inject.Inject

class LoginValidation @Inject constructor(
    private val stringProvider: AndroidStringProvider
) {

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

}