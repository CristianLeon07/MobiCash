package com.example.mobicash.domain.usecase

import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


// ADD USER

class AddUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserModel) {
        userRepository.add(user)
    }
}


// UPDATE USER

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserModel) {
        userRepository.update(user)
    }
}


// DELETE USER

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserModel) {
        userRepository.delete(user)
    }
}


// GET ALL USERS

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<UserModel>> =
        userRepository.getUsers()
}



// LOGIN (BUSCAR POR HASH DEL USER)

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: String): UserModel? {
        return userRepository.getUserByHash(user)
    }
}



