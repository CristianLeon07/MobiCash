package com.example.mobicash.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey
    val user: String,
    val email: String,
    val name: String,
    val pinHashed: String,         // Se almacena el hash del PIN
    val photo: String? = null,
    val userHashed: String,
    )