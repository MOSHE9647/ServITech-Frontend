package com.moviles.servitech.common

/**
 * Utility object for phone number operations.
 * This object provides methods to normalize and format phone numbers
 * for display purposes.
 */
object PhoneUtils {

    /**
     * Normalizes a phone input string to a standard format.
     * This function ensures that the phone number
     * is in the format of +506 followed by 8 digits.
     */
    fun normalizePhoneInput(input: String): String {
        // Extract only digits from the input
        val digits = input.filter { it.isDigit() }

        // Deletes the prefix "506" if it exists (as part of +506 or written manually)
        val cleaned = when {
            digits.startsWith("+506") -> digits.removePrefix("+506")
            else -> digits
        }

        // Take the last 8 digits to ensure we have a valid phone number
        val last8 = cleaned.takeLast(8)

        return "+506$last8"
    }

    /**
     * Formats a phone number for display.
     * This function formats the phone number
     * to a more readable format with spaces.
     *
     * @param phone The phone number string to format.
     * @return A formatted phone number string.
     */
    fun formatPhoneForDisplay(phone: String): String {
        val digits = phone.filter { it.isDigit() }

        // Remove the prefix "506" only if it exists and is followed by more than 8 digits
        val cleaned = if (digits.startsWith("506")) {
            digits.removePrefix("506")
        } else {
            digits
        }

        return when (cleaned.length) {
            0 -> ""
            in 1..3 -> "+506 $cleaned"
            in 4..7 -> "+506 ${cleaned.substring(0, cleaned.length)}"
            in 8..Int.MAX_VALUE -> "+506 ${cleaned.substring(0, 4)} ${cleaned.substring(4, 8)}"
            else -> "+506"
        }
    }

}