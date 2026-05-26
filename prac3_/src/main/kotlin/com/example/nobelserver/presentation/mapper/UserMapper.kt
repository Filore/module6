package com.example.nobelserver.presentation.mapper

import com.example.nobelserver.domain.model.User
import com.example.nobelserver.presentation.dto.UserResponse

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = id,
        username = username,
        role = role
    )
}
