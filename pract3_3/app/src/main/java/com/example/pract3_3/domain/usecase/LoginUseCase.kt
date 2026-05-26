package com.example.pract3_3.domain.usecase

import com.example.pract3_3.domain.model.AuthUser
import com.example.pract3_3.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ): AuthUser {
        return repository.login(
            username = username,
            password = password
        )
    }
}