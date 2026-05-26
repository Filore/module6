package com.example.nobelserver.plugins

import com.example.nobelserver.presentation.dto.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureHTTP() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled server error", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse("Internal server error")
            )
        }
    }
}
