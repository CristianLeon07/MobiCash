package com.example.mobicash.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val user: String,  // Se almacena tal cual el usuario original (sin hash)

    @ColumnInfo(name = "user_hashed")
    val userHashed: String, // Se almacena el hash del usuario (para login)

    val email: String,
    val name: String,

    @ColumnInfo(name = "pin_hashed")
    val pinHashed: String,         // Se almacena el hash del PIN

    val photo: String? = null
)

