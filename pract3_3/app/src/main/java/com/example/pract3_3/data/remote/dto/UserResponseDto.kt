package com.example.pract3_3.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsersResponseDto(
    val users: List<UserDto> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)