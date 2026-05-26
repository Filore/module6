package com.example.pract3_3.domain.usecase

import com.example.pract3_3.domain.model.User
import com.example.pract3_3.domain.repository.UsersRepository

class GetUsersUseCase(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(): List<User> {
        return repository.getUsers()
    }
}