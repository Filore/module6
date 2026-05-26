package com.example.pract3_2.domain.usecase

import com.example.pract3_2.domain.repository.NobelRepository

class LoginUseCase(
    private val repository: NobelRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ) {
        repository.login(
            username = username,
            password = password
        )
    }
}
