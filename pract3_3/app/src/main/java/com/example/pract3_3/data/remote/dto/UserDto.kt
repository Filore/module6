package com.example.pract3_3.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int = 0,
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = ""
)