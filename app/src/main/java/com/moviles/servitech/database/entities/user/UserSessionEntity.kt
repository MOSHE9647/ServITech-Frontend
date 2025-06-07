package com.moviles.servitech.database.entities.user

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants.SESSION_TABLE

/**
 * Entity representing a user session in the database.
 * This entity is used to store the current user's session information,
 * including user details, authentication token, and expiration time.
 *
 * @param id Unique identifier for the session (only one session is allowed).
 * @param user The user associated with this session.
 * @param token The authentication token for the session.
 * @param expiresIn The expiration time of the token in milliseconds.
 */
@Entity(tableName = SESSION_TABLE)
data class UserSessionEntity(
    @PrimaryKey val id: Int = 1,
    @Embedded(prefix = "user_") val user: UserEntity,
    val token: String,
    val expiresIn: Long
)