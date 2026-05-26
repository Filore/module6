package com.example.pract3_3.data.repository

import com.example.pract3_3.data.local.TokenStorage
import com.example.pract3_3.data.mapper.toDomain
import com.example.pract3_3.data.remote.api.DummyJsonApi
import com.example.pract3_3.domain.model.AuthUser
import com.example.pract3_3.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val api: DummyJsonApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String
    ): AuthUser {
        val response = api.login(
            username = username,
            password = password
        )

        if (response.accessToken.isBlank()) {
            throw IllegalStateException("Токен не получен")
        }

        tokenStorage.saveToken(response.accessToken)

        return response.toDomain()
    }

    override suspend fun logout() {
        tokenStorage.clearToken()
    }

    override suspend fun getToken(): String? {
        return tokenStorage.tokenFlow.first()
    }
}