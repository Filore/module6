package com.example.nobelserver.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    val issuer: String = "nobel-prize-api",
    val audience: String = "nobel-prize-api-users",
    val realm: String = "nobel-prize-api",
    private val secret: String = "nobel-prize-api-secret-key-32-symbols"
) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateToken(username: String): String {
        val now = System.currentTimeMillis()

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + TOKEN_LIFETIME_MILLIS))
            .sign(algorithm)
    }

    companion object {
        private const val TOKEN_LIFETIME_MILLIS = 30 * 60 * 1000L
    }
}
