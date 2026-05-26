package com.example.pract3_3.data.remote.api

import com.example.pract3_3.data.remote.dto.LoginRequestDto
import com.example.pract3_3.data.remote.dto.LoginResponseDto
import com.example.pract3_3.data.remote.dto.UserDto
import com.example.pract3_3.data.remote.dto.UsersResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class DummyJsonApi(
    private val client: HttpClient
) {
    suspend fun login(
        username: String,
        password: String
    ): LoginResponseDto {
        return client.post("https://dummyjson.com/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequestDto(
                    username = username,
                    password = password
                )
            )
        }.body()
    }

    suspend fun getUsers(
        token: String
    ): UsersResponseDto {
        return client.get("https://dummyjson.com/users") {
            header(
                HttpHeaders.Authorization,
                "Bearer $token"
            )
        }.body()
    }

    suspend fun getUserById(
        id: Int,
        token: String
    ): UserDto {
        return client.get("https://dummyjson.com/users/$id") {
            header(
                HttpHeaders.Authorization,
                "Bearer $token"
            )
        }.body()
    }
}