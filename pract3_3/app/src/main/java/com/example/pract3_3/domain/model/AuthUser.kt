package com.example.pract3_3.domain.model

data class AuthUser(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val image: String,
    val token: String
)