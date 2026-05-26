package com.example.pract3_3.domain.usecase

import com.example.pract3_3.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() {
        repository.logout()
    }
}