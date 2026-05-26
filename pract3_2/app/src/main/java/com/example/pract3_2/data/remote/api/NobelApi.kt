package com.example.pract3_2.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class NobelApi(
    private val client: HttpClient,
    private val baseUrl: String = BASE_URL
) {
    private var authToken: String? = null

    suspend fun login(
        username: String,
        password: String
    ) {
        authToken = loginRequired(username, password)
    }

    suspend fun getNobelPrizes(
        year: String?,
        category: String?
    ): JsonElement {
        return authorizedJsonRequest {
            requestPrizes(year, category)
        }
    }

    suspend fun getFavoritePrizes(): JsonElement {
        return authorizedJsonRequest {
            client.get("$baseUrl/users/me/prizes") {
                addAuthorizationHeader()
            }
        }
    }

    suspend fun addFavoritePrize(prizeId: String) {
        authorizedUnitRequest {
            client.post("$baseUrl/users/me/prizes/$prizeId") {
                addAuthorizationHeader()
            }
        }
    }

    suspend fun removeFavoritePrize(prizeId: String) {
        authorizedUnitRequest {
            client.delete("$baseUrl/users/me/prizes/$prizeId") {
                addAuthorizationHeader()
            }
        }
    }

    private suspend fun requestPrizes(
        year: String?,
        category: String?
    ): HttpResponse {
        return client.get("$baseUrl/prizes") {
            addAuthorizationHeader()

            if (!year.isNullOrBlank()) {
                parameter("year", year)
                parameter("awardYear", year)
            }

            if (!category.isNullOrBlank()) {
                parameter("category", category)
            }
        }
    }

    private suspend fun authorizedJsonRequest(block: suspend () -> HttpResponse): JsonElement {
        val response = authorizedResponse(block)
        val body = response.bodyAsText()
        return networkJson.parseToJsonElement(body)
    }

    private suspend fun authorizedUnitRequest(block: suspend () -> HttpResponse) {
        authorizedResponse(block)
    }

    private suspend fun authorizedResponse(block: suspend () -> HttpResponse): HttpResponse {
        require(!authToken.isNullOrBlank()) {
            "Сначала выполните вход"
        }

        var response = block()

        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            throw IllegalStateException(
                "Ошибка сервера ${response.status.value}: ${body.ifBlank { response.status.description }}"
            )
        }

        return response
    }

    private fun io.ktor.client.request.HttpRequestBuilder.addAuthorizationHeader() {
        authToken?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    private suspend fun loginRequired(
        username: String,
        password: String
    ): String {
        val response = client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    username = username,
                    password = password
                )
            )
        }

        val body = response.bodyAsText()

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Не удалось авторизоваться: ${response.status.value} ${body.ifBlank { response.status.description }}"
            )
        }

        return body.tokenValue()
            ?: throw IllegalStateException("Сервер авторизации не вернул токен")
    }

    private fun String.tokenValue(): String? {
        val trimmed = trim()
        if (trimmed.isBlank()) {
            return null
        }

        return runCatching {
            networkJson.parseToJsonElement(trimmed).tokenValue()
        }.getOrNull() ?: trimmed.removePrefix("Bearer ").takeIf { it.isNotBlank() }
    }

    private fun JsonElement.tokenValue(): String? {
        return when (this) {
            is JsonPrimitive -> contentOrNull?.takeIf { it.isNotBlank() }
            is JsonObject -> tokenKeys.firstNotNullOfOrNull { key ->
                this[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            } ?: this["data"]?.tokenValue()
            else -> null
        }
    }

    private fun HttpStatusCode.isSuccess(): Boolean {
        return value in 200..299
    }

    private companion object {
        const val BASE_URL = "http://10.0.2.2:8080"

        val networkJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        val tokenKeys = listOf(
            "token",
            "accessToken",
            "access_token",
            "access-token",
            "jwt"
        )
    }
}

@Serializable
private data class LoginRequest(
    val username: String,
    val password: String
)
