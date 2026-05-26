package com.example.nobelserver.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresInMinutes: Int = 30
)

@Serializable
data class ErrorResponse(
    val message: String
)
