package com.example.nobelserver.domain.repository

import com.example.nobelserver.domain.model.User

interface UserRepository {
    fun findByUsername(username: String): User?
    fun findPasswordHash(username: String): String?
}
