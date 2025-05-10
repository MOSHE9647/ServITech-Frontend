package com.moviles.servitech.database.entities

import androidx.room.Entity
import com.moviles.servitech.common.Constants.USER_TABLE

@Entity(tableName = USER_TABLE)
data class UserEntity(
    val id: Int,
    val role: String,
    val name: String,
    val email: String,
    val phone: String? = null
)
