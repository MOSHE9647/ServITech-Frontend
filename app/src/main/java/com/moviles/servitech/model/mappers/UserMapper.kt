package com.moviles.servitech.model.mappers

import com.moviles.servitech.database.entities.user.UserEntity
import com.moviles.servitech.database.entities.user.UserSessionEntity
import com.moviles.servitech.model.User
import com.moviles.servitech.model.enums.UserRole

/**
 * Maps a [UserSessionEntity] to a [User].
 * This function extracts the user information from the session entity
 * and returns a [User] object.
 */
fun UserSessionEntity.toUser(): User {
    return User(
        id = this.user.id,
        name = this.user.name,
        email = this.user.email,
        phone = this.user.phone,
        role = this.user.role.ifEmpty { UserRole.GUEST.name })
}

/**
 * Maps a [User] to a [UserEntity].
 * This function converts the user model to a database entity.
 *
 * @return A [UserEntity] representing the user.
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id ?: 0,
        name = this.name,
        email = this.email,
        phone = this.phone ?: "",
        role = this.role
    )
}