package com.example.pract3_3.domain.usecase

import com.example.pract3_3.domain.model.User
import com.example.pract3_3.domain.repository.UsersRepository

class GetUserByIdUseCase(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(id: Int): User {
        return repository.getUserById(id)
    }
}