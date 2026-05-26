package com.example.pract3_3.data.repository

import com.example.pract3_3.data.mapper.toDomain
import com.example.pract3_3.data.remote.api.DummyJsonApi
import com.example.pract3_3.domain.model.User
import com.example.pract3_3.domain.repository.AuthRepository
import com.example.pract3_3.domain.repository.UsersRepository

class UsersRepositoryImpl(
    private val api: DummyJsonApi,
    private val authRepository: AuthRepository
) : UsersRepository {

    override suspend fun getUsers(): List<User> {
        val token = authRepository.getToken()
            ?: throw IllegalStateException("Пользователь не авторизован")

        return api.getUsers(token).users.map { userDto ->
            userDto.toDomain()
        }
    }

    override suspend fun getUserById(id: Int): User {
        val token = authRepository.getToken()
            ?: throw IllegalStateException("Пользователь не авторизован")

        return api.getUserById(
            id = id,
            token = token
        ).toDomain()
    }
}