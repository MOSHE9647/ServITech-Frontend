package com.moviles.servitech.services.validation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.moviles.servitech.R
import com.moviles.servitech.common.PhoneUtils
import com.moviles.servitech.core.providers.AndroidStringProvider
import com.moviles.servitech.model.Image
import com.moviles.servitech.model.enums.RepairStatus
import java.time.LocalDate
import javax.inject.Inject

class RepairRequestValidation @Inject constructor(
    private val stringProvider: AndroidStringProvider
) {

    /**
     * List of valid repair statuses.
     * This is used to validate the repair status input.
     */
    private val validStatuses = RepairStatus.entries.map { it.value }

    /**
     * Validates the customer name input.
     *
     * @param name The customer name to validate.
     * @return A [ValidationResult] indicating whether the name is valid or not,
     * with appropriate error messages for empty or too short names.
     */
    fun validateCustomerName(name: String): ValidationResult {
        return when {
            name.isBlank() -> invalid(stringProvider.getString(R.string.name_empty_error))
            name.length < 3 -> invalid(stringProvider.getString(R.string.name_length_error))
            else -> valid()
        }
    }

    /**
     * Validates the customer phone input.
     *
     * @param phone The customer phone number to validate.
     * @return A [ValidationResult] indicating whether the phone number is valid or not,
     * with appropriate error messages for empty or too short phone numbers.
     */
    fun validateCustomerPhone(phone: String): ValidationResult {
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
     * Validates the customer email input.
     *
     * @param email The customer email to validate.
     * @return A [ValidationResult] indicating whether the email is valid or not,
     * with appropriate error messages for empty or invalid emails.
     */
    fun validateCustomerEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> invalid(stringProvider.getString(R.string.email_empty_error))
            !email.matches(
                Regex(pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|net|org|edu|gov|mil|info|io|co)$")
            ) -> invalid(stringProvider.getString(R.string.email_invalid_error))

            else -> valid()
        }
    }

    /**
     * Validates the article name input.
     *
     * @param name The article name to validate.
     * @return A [ValidationResult] indicating whether the article name is valid or not,
     * with appropriate error messages for empty or too short names.
     */
    fun validateArticleName(name: String) =
        validateString(
            name, 3,
            stringProvider.getString(R.string.article_name_required),
            stringProvider.getString(R.string.article_name_too_short)
        )

    /**
     * Validates the article type input.
     *
     * @param type The article type to validate.
     * @return A [ValidationResult] indicating whether the article type is valid or not,
     * with appropriate error messages for empty or too short types.
     */
    fun validateArticleType(type: String) =
        validateString(
            type, 3,
            stringProvider.getString(R.string.article_type_required),
            stringProvider.getString(R.string.article_type_too_short)
        )

    /**
     * Validates the article brand input.
     *
     * @param brand The article brand to validate.
     * @return A [ValidationResult] indicating whether the article brand is valid or not,
     * with appropriate error messages for empty or too short brands.
     */
    fun validateArticleBrand(brand: String) =
        validateString(
            brand, 2,
            stringProvider.getString(R.string.article_brand_required),
            stringProvider.getString(R.string.article_brand_too_short)
        )

    /**
     * Validates the article model input.
     *
     * @param model The article model to validate.
     * @return A [ValidationResult] indicating whether the article model is valid or not,
     * with appropriate error messages for empty or too short models.
     */
    fun validateArticleModel(model: String) =
        validateString(
            model, 2,
            stringProvider.getString(R.string.article_model_required),
            stringProvider.getString(R.string.article_model_too_short)
        )

    /**
     * Validates the article serial input.
     *
     * @param serial The article serial number to validate.
     * @return A [ValidationResult] indicating whether the article serial is valid or not,
     * with appropriate error messages for empty or too short serial numbers.
     */
    fun validateArticleSerial(serial: String?): ValidationResult {
        return if (!serial.isNullOrBlank() && serial.length < 6)
            invalid(stringProvider.getString(R.string.article_serial_too_short))
        else valid()
    }

    /**
     * Validates the article accessories input.
     *
     * @param acc The article accessories to validate.
     * @return A [ValidationResult] indicating whether the article accessories are valid or not,
     * with appropriate error messages for too short accessories.
     */
    fun validateAccessories(acc: String?): ValidationResult {
        return if (!acc.isNullOrBlank() && acc.length < 3)
            invalid(stringProvider.getString(R.string.article_accessories_too_short))
        else valid()
    }

    /**
     * Validates the problem description input.
     *
     * @param problem The problem description to validate.
     * @return A [ValidationResult] indicating whether the problem description is valid or not,
     * with appropriate error messages for empty or too short descriptions.
     */
    fun validateProblem(problem: String) = validateString(
        problem, 3,
        stringProvider.getString(R.string.article_problem_required),
        stringProvider.getString(R.string.article_problem_too_short)
    )

    /**
     * Validates the repair status input.
     *
     * @param status The repair status to validate.
     * @return A [ValidationResult] indicating whether the repair status is valid or not,
     * with appropriate error messages for empty or invalid statuses.
     */
    fun validateRepairStatus(status: String): ValidationResult {
        return when {
            status.isBlank() -> invalid(stringProvider.getString(R.string.repair_status_required))
            status !in validStatuses -> invalid(stringProvider.getString(R.string.repair_status_invalid))
            else -> valid()
        }
    }

    /**
     * Validates the repair details input.
     *
     * @param details The repair details to validate.
     * @return A [ValidationResult] indicating whether the repair details are valid or not,
     * with appropriate error messages for too short details.
     */
    fun validateRepairDetails(details: String?): ValidationResult {
        return if (!details.isNullOrBlank() && details.length < 3)
            invalid(stringProvider.getString(R.string.repair_details_too_short))
        else valid()
    }

    /**
     * Validates the repair price input.
     *
     * @param price The repair price to validate.
     * @return A [ValidationResult] indicating whether the repair price is valid or not,
     * with appropriate error messages for invalid prices.
     */
    fun validateRepairPrice(price: Double?): ValidationResult {
        return if (price != null && price < 0)
            invalid(stringProvider.getString(R.string.repair_price_invalid))
        else valid()
    }

    /**
     * Validates the received at date input.
     *
     * @param date The received at date to validate.
     * @return A [ValidationResult] indicating whether the received at date is valid or not,
     * with appropriate error messages for empty dates.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateReceivedAt(date: String?): ValidationResult {
        if (date.isNullOrEmpty()) {
            return invalid(stringProvider.getString(R.string.received_at_required))
        }
        return try {
            val parsedDate = LocalDate.parse(date)
            when {
                parsedDate.isAfter(LocalDate.now()) ->
                    invalid(stringProvider.getString(R.string.received_at_future_error))

                else -> valid()
            }
        } catch (e: Exception) {
            Log.e("RepairRequestValidation", "Invalid date format: $date", e)
            invalid(stringProvider.getString(R.string.received_at_invalid_format))
        }
    }

    /**
     * Validates the repaired at date input.
     *
     * @param date The repaired at date to validate.
     * @param receivedAt The received at date to compare against.
     * @return A [ValidationResult] indicating whether the repaired at date is valid or not,
     * with appropriate error messages for empty dates.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun validateRepairedAt(date: String?, receivedAt: String?): ValidationResult {
        if (date == null) return valid()
        if (receivedAt == null) return invalid(stringProvider.getString(R.string.received_at_required))
        return try {
            val repairedAtDate = LocalDate.parse(date)
            val receivedAtDate = LocalDate.parse(receivedAt)
            when {
                repairedAtDate.isAfter(LocalDate.now()) ->
                    invalid(stringProvider.getString(R.string.repaired_at_future_error))

                repairedAtDate.isBefore(receivedAtDate) ->
                    invalid(stringProvider.getString(R.string.repaired_at_before_received_error))

                else -> valid()
            }
        } catch (e: Exception) {
            Log.e("RepairRequestValidation", "Invalid date format: $date", e)
            invalid(stringProvider.getString(R.string.repaired_at_invalid_format))
        }
    }

    /**
     * Validates a semicolon separated string of image names.
     * This method checks if each image name ends with a valid
     * extension (.jpg, .jpeg, .png).
     *
     * @param imagesString The string containing image names separated by semicolons.
     * @return A [ValidationResult] indicating whether the image names are valid or not,
     */
    fun validateImagesNames(imagesString: String): ValidationResult {
        if (imagesString.isBlank()) return valid()
        val validExtensions = listOf(".jpg", ".jpeg", ".png")
        val images = imagesString.split(";").map { it.trim() }
        val invalidImages = images.filter { image ->
            image.isBlank() || validExtensions.none { image.lowercase().endsWith(it) }
        }
        return if (invalidImages.isNotEmpty()) {
            invalid(stringProvider.getString(R.string.image_format_error))
        } else {
            valid()
        }
    }

    fun validateImagesList(images: List<Image>?): ValidationResult {
        if (images.isNullOrEmpty()) return valid()
        val validExtensions = listOf(".jpg", ".jpeg", ".png")

        return when {
            images.any { it.file == null } -> invalid(stringProvider.getString(R.string.image_empty_error))
            images.any { it.title.isNullOrEmpty() } -> invalid(stringProvider.getString(R.string.image_empty_error))
            images.any { validExtensions.none { ext -> it.title!!.lowercase().endsWith(ext) } } -> {
                invalid(stringProvider.getString(R.string.image_format_error))
            }

            else -> valid()
        }
    }

}