package com.example.pract3_3.domain.repository

import com.example.pract3_3.domain.model.User

interface UsersRepository {

    suspend fun getUsers(): List<User>

    suspend fun getUserById(id: Int): User
}