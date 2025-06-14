package com.moviles.servitech.model.enums

enum class UserRole(value: String) {
    ADMIN("admin"),
    EMPLOYEE("employee"),
    GUEST("guest"),
    USER("user");

    fun label(): String {
        return when (this) {
            ADMIN -> "Administrator"
            EMPLOYEE -> "Employee"
            GUEST -> "Guest"
            USER -> "User"
        }
    }

    fun isAdmin(): Boolean = this == ADMIN
    fun isEmployee(): Boolean = this == EMPLOYEE
    fun isGuest(): Boolean = this == GUEST
    fun isUser(): Boolean = this == USER
}