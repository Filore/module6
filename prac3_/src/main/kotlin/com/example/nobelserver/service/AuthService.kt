package com.example.nobelserver.service

import com.example.nobelserver.domain.model.User
import com.example.nobelserver.domain.repository.UserRepository
import com.example.nobelserver.security.JwtService
import com.example.nobelserver.security.PasswordHasher

class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {
    fun login(username: String, password: String): String? {
        val passwordHash = userRepository.findPasswordHash(username) ?: return null
        if (!PasswordHasher.verify(password, passwordHash)) return null

        return jwtService.generateToken(username)
    }

    fun getUser(username: String): User? {
        return userRepository.findByUsername(username)
    }
}
