package com.example.pract3_3.data.mapper

import com.example.pract3_3.data.remote.dto.LoginResponseDto
import com.example.pract3_3.data.remote.dto.UserDto
import com.example.pract3_3.domain.model.AuthUser
import com.example.pract3_3.domain.model.User

fun LoginResponseDto.toDomain(): AuthUser {
    return AuthUser(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        image = image,
        token = accessToken
    )
}

fun UserDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        image = image
    )
}