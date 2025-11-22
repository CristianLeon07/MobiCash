package com.example.mobicash.data.repository

import com.example.mobicash.core.utils.HashUtils
import com.example.mobicash.data.local.UserDao
import com.example.mobicash.data.mapper.toData
import com.example.mobicash.data.mapper.toDomain
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUsers(): Flow<List<UserModel>> =
        userDao.getUsers().map { list -> list.map { it.toDomain() } }

    override suspend fun getUserByEmail(email: String): UserModel? =
        userDao.getUserByEmail(email)?.toDomain()

    override suspend fun getUserByHash(user: String): UserModel? {
        val hash = HashUtils.sha256(user)
        return userDao.getUserByHash(hash)?.toDomain()
    }

    override suspend fun getUserByUser(user: String): UserModel? =
        userDao.getUserByUser(user)?.toDomain()

    override suspend fun add(user: UserModel) {
        userDao.addUser(user.toData())
    }

    override suspend fun update(user: UserModel) {
        userDao.updateUser(user.toData())
    }

    override suspend fun delete(user: UserModel) {
        userDao.deleteUser(user.toData())
    }
}
