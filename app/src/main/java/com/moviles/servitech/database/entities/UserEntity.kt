package com.moviles.servitech.database.entities

import androidx.room.Entity
import com.moviles.servitech.common.Constants.USER_TABLE

/**
 * Entity representing a user in the database.
 * This entity is used to store user information such as id, role, name, email, and phone.
 *
 * @param id Unique identifier for the user.
 * @param role The role of the user (e.g., admin, user).
 * @param name The name of the user.
 * @param email The email address of the user.
 * @param phone Optional phone number of the user.
 */
@Entity(tableName = USER_TABLE)
data class UserEntity(
    val id: Int,
    val role: String,
    val name: String,
    val email: String,
    val phone: String? = null
)
