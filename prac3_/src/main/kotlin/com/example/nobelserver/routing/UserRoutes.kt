package com.example.nobelserver.routing

import com.example.nobelserver.domain.usecase.ManageFavoritePrizesUseCase
import com.example.nobelserver.plugins.AUTH_JWT
import com.example.nobelserver.presentation.dto.ErrorResponse
import com.example.nobelserver.presentation.mapper.toResponse
import com.example.nobelserver.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureUserRoutes(
    authService: AuthService,
    favoritePrizesUseCase: ManageFavoritePrizesUseCase
) {
    routing {
        authenticate(AUTH_JWT) {
            route("/users/me") {
                get {
                    val username = call.usernameFromToken()
                    val user = authService.getUser(username)

                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("User was not found"))
                        return@get
                    }

                    call.respond(user.toResponse())
                }

                get("/prizes") {
                    val username = call.usernameFromToken()
                    val favorites = favoritePrizesUseCase.getFavorites(username).map { it.toResponse() }
                    call.respond(favorites)
                }

                post("/prizes/{prizeId}") {
                    val username = call.usernameFromToken()
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()

                    if (prizeId == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Prize id must be a number"))
                        return@post
                    }

                    val added = favoritePrizesUseCase.addFavorite(username, prizeId)
                    if (!added) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("User or prize was not found"))
                        return@post
                    }

                    call.respond(HttpStatusCode.Created, ErrorResponse("Prize was added to favorites"))
                }

                delete("/prizes/{prizeId}") {
                    val username = call.usernameFromToken()
                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()

                    if (prizeId == null) {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Prize id must be a number"))
                        return@delete
                    }

                    val deleted = favoritePrizesUseCase.deleteFavorite(username, prizeId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("Favorite prize was not found"))
                        return@delete
                    }

                    call.respond(HttpStatusCode.OK, ErrorResponse("Prize was removed from favorites"))
                }
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.usernameFromToken(): String {
    return principal<JWTPrincipal>()!!.payload.getClaim("username").asString()
}
