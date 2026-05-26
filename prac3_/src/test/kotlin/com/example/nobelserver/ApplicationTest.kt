package com.example.nobelserver

import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun `login returns jwt token`() = testApplication {
        useTestDatabase("login")
        application {
            module()
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"admin","password":"password"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val token = Json.parseToJsonElement(response.bodyAsText()).jsonObject["token"]?.jsonPrimitive?.content
        assertTrue(!token.isNullOrBlank())
    }

    @Test
    fun `prizes are public and user favorites are protected by jwt`() = testApplication {
        useTestDatabase("favorites")
        application {
            module()
        }

        val publicPrizesResponse = client.get("/prizes")
        assertEquals(HttpStatusCode.OK, publicPrizesResponse.status)
        assertTrue(publicPrizesResponse.bodyAsText().contains("physics"))

        val unauthorizedResponse = client.get("/users/me/prizes")
        assertEquals(HttpStatusCode.Unauthorized, unauthorizedResponse.status)

        val loginResponse = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"student","password":"qwerty123"}""")
        }
        val token = Json.parseToJsonElement(loginResponse.bodyAsText()).jsonObject["token"]!!.jsonPrimitive.content

        val addFavoriteResponse = client.post("/users/me/prizes/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.Created, addFavoriteResponse.status)

        val favoritesResponse = client.get("/users/me/prizes") {
            bearerAuth(token)
        }

        assertEquals(HttpStatusCode.OK, favoritesResponse.status)
        assertTrue(favoritesResponse.bodyAsText().contains("physics"))
    }

    private fun useTestDatabase(name: String) {
        System.setProperty(
            "DATABASE_URL",
            "jdbc:h2:mem:$name;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
        )
    }
}
