package com.example.nobelserver.plugins

import com.example.nobelserver.security.JwtService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

const val AUTH_JWT = "auth-jwt"

fun Application.configureSecurity(jwtService: JwtService) {
    install(Authentication) {
        jwt(AUTH_JWT) {
            realm = jwtService.realm
            verifier(jwtService.verifier)
            validate { credential ->
                val username = credential.payload.getClaim("username").asString()
                if (username.isNullOrBlank()) null else JWTPrincipal(credential.payload)
            }
        }
    }
}
