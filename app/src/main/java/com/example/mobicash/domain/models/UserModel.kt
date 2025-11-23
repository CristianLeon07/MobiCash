package com.example.mobicash.domain.models

data class UserModel(
    val user: String,
    val userHashed: String,
    val email: String,
    val name: String,
    val pin: String,
    val photo: String? = null
)
