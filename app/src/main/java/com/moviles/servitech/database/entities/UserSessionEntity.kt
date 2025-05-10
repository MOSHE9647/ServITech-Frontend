package com.moviles.servitech.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants.SESSION_TABLE

@Entity(tableName = SESSION_TABLE)
data class UserSessionEntity(
    @PrimaryKey val id: Int = 1, // Only one session is allowed
    @Embedded(prefix = "user_") val user: UserEntity,
    val token: String,
    val expiresIn: Long
)