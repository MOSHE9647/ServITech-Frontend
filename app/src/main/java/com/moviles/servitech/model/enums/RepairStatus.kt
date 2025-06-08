package com.moviles.servitech.model.enums

import android.content.Context
import com.moviles.servitech.R
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Enum class representing the status of a repair request.
 *
 * This enum is used to categorize the different stages of a repair process,
 * including pending, in progress, waiting for parts, completed, delivered, and cancelled.
 *
 * It provides a method to get a user-friendly label for each repair status,
 * as well as a method to convert the status to a string representation
 * for API communication.
 *
 * @property value The string representation of the repair status.
 */
enum class RepairStatus(val value: String) {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    WAITING_PARTS("waiting_parts"),
    COMPLETED("completed"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");

    /**
     * Returns a user-friendly label for the repair status.
     *
     * @param context The application context used to retrieve string resources.
     * @return A string representing the repair status label.
     */
    fun label(@ApplicationContext context: Context): String {
        return when (this) {
            PENDING -> context.getString(R.string.repair_status_pending)
            IN_PROGRESS -> context.getString(R.string.repair_status_in_progress)
            WAITING_PARTS -> context.getString(R.string.repair_status_waiting_parts)
            COMPLETED -> context.getString(R.string.repair_status_completed)
            DELIVERED -> context.getString(R.string.repair_status_delivered)
            CANCELLED -> context.getString(R.string.repair_status_cancelled)
        }
    }

    /**
     * Converts the repair status to a string representation for API communication.
     *
     * @return A string representing the repair status suitable for API requests.
     */
    fun toApiString(): String {
        return when (this) {
            PENDING -> "pending"
            IN_PROGRESS -> "in_progress"
            WAITING_PARTS -> "waiting_parts"
            COMPLETED -> "completed"
            DELIVERED -> "delivered"
            CANCELLED -> "cancelled"
        }
    }
}