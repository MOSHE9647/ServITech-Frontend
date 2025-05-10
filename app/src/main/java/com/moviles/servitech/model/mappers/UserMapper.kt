package com.moviles.servitech.model.mappers

import com.moviles.servitech.common.Constants.GUEST_ROLE
import com.moviles.servitech.database.entities.UserEntity
import com.moviles.servitech.database.entities.UserSessionEntity
import com.moviles.servitech.model.User
import kotlin.text.ifEmpty

fun UserSessionEntity.toUser(): User {
    return User(
        id = this.user.id,
        name = this.user.name,
        email = this.user.email,
        phone = this.user.phone,
        role = this.user.role.ifEmpty { GUEST_ROLE })
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id ?: 0,
        name = this.name,
        email = this.email,
        phone = this.phone ?: "",
        role = this.role
    )
}