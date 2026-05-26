package com.example.nobelserver.security

import java.security.MessageDigest

object PasswordHasher {
    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, passwordHash: String): Boolean {
        return hash(password) == passwordHash
    }
}
