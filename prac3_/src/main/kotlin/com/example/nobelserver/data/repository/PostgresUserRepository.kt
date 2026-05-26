package com.example.nobelserver.data.repository

import com.example.nobelserver.data.database.UsersTable
import com.example.nobelserver.domain.model.User
import com.example.nobelserver.domain.repository.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresUserRepository : UserRepository {
    override fun findByUsername(username: String): User? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.username eq username }
            .firstOrNull()
            ?.toUser()
    }

    override fun findPasswordHash(username: String): String? = transaction {
        UsersTable
            .selectAll()
            .where { UsersTable.username eq username }
            .firstOrNull()
            ?.get(UsersTable.passwordHash)
    }

    private fun ResultRow.toUser(): User {
        return User(
            id = this[UsersTable.id].value,
            username = this[UsersTable.username],
            role = this[UsersTable.role]
        )
    }
}
