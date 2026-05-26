package com.example.pract3_3.domain.repository

import com.example.pract3_3.domain.model.AuthUser

interface AuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): AuthUser

    suspend fun logout()

    suspend fun getToken(): String?
}