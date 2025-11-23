package com.example.mobicash.data.mapper

import com.example.mobicash.core.utils.HashUtils
import com.example.mobicash.data.local.entities.UserEntity
import com.example.mobicash.domain.models.UserModel

/**
 * Convenciones:
 * - UserEntity.user -> el username en texto plano (para mostrar / verificar duplicados en registro)
 * - UserEntity.userHashed -> sha256(user) -> usado para login (búsqueda rápida)
 * - UserEntity.pinHashed -> sha256(pin) -> usado para validar PIN en login
 *
 * En Domain model (UserModel) dejamos:
 * - user: String  (texto plano)
 * - pin: String   (guardamos el HASH en el domain porque no queremos exponer el pin en claro)
 */

fun UserEntity.toDomain(): UserModel = UserModel(
    user = this.user,
    userHashed = this.userHashed,
    email = this.email,
    name = this.name,
    pin = this.pinHashed,
    photo = this.photo
)


fun UserModel.toData(): UserEntity {
    val userHash = HashUtils.sha256(this.user)
    val pinHash = HashUtils.sha256(this.pin)

    return UserEntity(
        user = this.user,
        userHashed = userHash,
        email = this.email,
        name = this.name,
        pinHashed = pinHash,
        photo = this.photo
    )
}

