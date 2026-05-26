package com.example.nobelserver.routing

import com.example.nobelserver.presentation.dto.ErrorResponse
import com.example.nobelserver.presentation.dto.LoginRequest
import com.example.nobelserver.presentation.dto.LoginResponse
import com.example.nobelserver.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAuthRoutes(authService: AuthService) {
    routing {
        post("/login") {
            login(authService)
        }

        route("/auth") {
            post("/login") {
                login(authService)
            }
        }
    }
}

private suspend fun io.ktor.server.routing.RoutingContext.login(authService: AuthService) {
    val request = call.receive<LoginRequest>()
    val token = authService.login(request.username, request.password)

    if (token == null) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = ErrorResponse("Invalid username or password")
        )
        return
    }

    call.respond(LoginResponse(token = token))
}
