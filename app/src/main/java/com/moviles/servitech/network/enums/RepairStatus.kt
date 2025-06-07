package com.moviles.servitech.network.enums

/**
 * Enum class representing the status of a repair request.
 */
enum class RepairStatus(val value: String) {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    WAITING_PARTS("waiting_parts"),
    COMPLETED("completed"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");

    companion object {
        /**
         * Returns the [RepairStatus] corresponding to the given value.
         *
         * @param value The string value of the repair status.
         * @return The [RepairStatus] corresponding to the value, or null if not found.
         */
        fun fromValue(value: String): RepairStatus? {
            return entries.find { it.value == value }
        }
    }

    /**
     * Returns the string representation of the repair status.
     *
     * @return The string value of the repair status.
     */
    override fun toString(): String {
        return value // Return the string representation of the enum value
    }

    /**
     * Returns the string representation of the repair status.
     *
     * @return The string value of the repair status.
     */
    fun toDisplayString(): String {
        return when (this) {
            PENDING -> "Pending"
            IN_PROGRESS -> "In Progress"
            WAITING_PARTS -> "Waiting for Parts"
            COMPLETED -> "Completed"
            DELIVERED -> "Delivered"
            CANCELLED -> "Cancelled"
        }
    }

    /**
     * Returns the string representation of the repair status.
     *
     * @return The string value of the repair status.
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