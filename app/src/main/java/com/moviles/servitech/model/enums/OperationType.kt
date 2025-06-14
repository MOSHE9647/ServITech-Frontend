package com.moviles.servitech.model.enums

/**
 * Enum class representing the type of operation performed on a database entity
 * for pending operations (synchronization).
 *
 * This enum is used to categorize operations such as insert, update, and delete.
 * It provides a method to get a label for each operation type.
 *
 * @property value The string representation of the operation type.
 */
enum class OperationType(value: String) {
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    /**
     * Returns a label for the operation type.
     *
     * @return A string representing the operation type label.
     */
    fun label(): String {
        return when (this) {
            INSERT -> "INSERT"
            UPDATE -> "UPDATE"
            DELETE -> "DELETE"
        }
    }

}