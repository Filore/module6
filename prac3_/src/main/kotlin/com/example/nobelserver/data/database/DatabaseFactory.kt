package com.example.nobelserver.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object DatabaseFactory {
    fun init() {
        Database.connect(createHikariDataSource())

        transaction {
            SchemaUtils.create(
                UsersTable,
                PrizesTable,
                LaureatesTable,
                UserPrizesTable
            )
        }

        DatabaseSeeder.seed()
    }

    private fun createHikariDataSource(): HikariDataSource {
        val databaseUrl = System.getProperty("DATABASE_URL")
            ?: System.getenv("DATABASE_URL")
            ?: error("Set DATABASE_URL to your Neon PostgreSQL connection string")
        val connectionSettings = databaseUrl.toConnectionSettings()

        val config = HikariConfig().apply {
            jdbcUrl = connectionSettings.jdbcUrl
            username = connectionSettings.username
            password = connectionSettings.password
            driverClassName = driverFor(jdbcUrl)
            maximumPoolSize = 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    private fun String.toConnectionSettings(): ConnectionSettings {
        if (startsWith("postgresql://") || startsWith("postgres://")) {
            val uri = URI(this)
            val userInfo = uri.userInfo?.split(":", limit = 2).orEmpty()
            val username = userInfo.getOrNull(0)?.urlDecode().orEmpty()
            val password = userInfo.getOrNull(1)?.urlDecode().orEmpty()
            val port = if (uri.port == -1) "" else ":${uri.port}"
            val query = uri.rawQuery?.let { "?$it" }.orEmpty()
            val jdbcUrl = "jdbc:postgresql://${uri.host}$port${uri.path}$query"

            return ConnectionSettings(
                jdbcUrl = jdbcUrl,
                username = username,
                password = password
            )
        }

        return when {
            startsWith("jdbc:") -> ConnectionSettings(this, null, null)
            else -> ConnectionSettings(this, null, null)
        }
    }

    private fun String.urlDecode(): String {
        return URLDecoder.decode(this, StandardCharsets.UTF_8)
    }

    private fun driverFor(jdbcUrl: String): String {
        return if (jdbcUrl.startsWith("jdbc:h2:")) {
            "org.h2.Driver"
        } else {
            "org.postgresql.Driver"
        }
    }

    private data class ConnectionSettings(
        val jdbcUrl: String,
        val username: String?,
        val password: String?
    )
}
