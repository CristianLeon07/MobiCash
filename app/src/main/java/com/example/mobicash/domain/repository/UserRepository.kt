package com.example.mobicash.domain.repository

import com.example.mobicash.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUsers(): Flow<List<UserModel>>

    suspend fun getUserByEmail(email: String): UserModel?

    suspend fun getUserByHash(user: String): UserModel?

    suspend fun getUserByUser(user: String): UserModel?  // ➤ AGREGADO

    suspend fun add(user: UserModel)

    suspend fun update(user: UserModel)

    suspend fun delete(user: UserModel)
}
