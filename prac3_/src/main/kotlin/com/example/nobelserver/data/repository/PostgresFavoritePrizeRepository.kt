package com.example.nobelserver.data.repository

import com.example.nobelserver.data.database.PrizesTable
import com.example.nobelserver.data.database.UserPrizesTable
import com.example.nobelserver.data.database.UsersTable
import com.example.nobelserver.domain.model.NobelPrize
import com.example.nobelserver.domain.repository.FavoritePrizeRepository
import com.example.nobelserver.domain.repository.NobelPrizeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class PostgresFavoritePrizeRepository(
    private val prizeRepository: NobelPrizeRepository
) : FavoritePrizeRepository {
    override fun getFavoritePrizes(username: String): List<NobelPrize> = transaction {
        val userId = findUserId(username) ?: return@transaction emptyList()

        UserPrizesTable
            .selectAll()
            .where { UserPrizesTable.userId eq userId }
            .mapNotNull { row -> prizeRepository.getPrizeById(row[UserPrizesTable.prizeId].value) }
    }

    override fun addFavoritePrize(username: String, prizeId: Int): Boolean = transaction {
        val userId = findUserId(username) ?: return@transaction false
        val prizeExists = PrizesTable
            .selectAll()
            .where { PrizesTable.id eq prizeId }
            .any()

        if (!prizeExists) return@transaction false

        UserPrizesTable.insertIgnore {
            it[UserPrizesTable.userId] = userId
            it[UserPrizesTable.prizeId] = prizeId
            it[addedAt] = LocalDateTime.now()
        }

        true
    }

    override fun deleteFavoritePrize(username: String, prizeId: Int): Boolean = transaction {
        val userId = findUserId(username) ?: return@transaction false

        UserPrizesTable.deleteWhere {
            (UserPrizesTable.userId eq userId) and
                (UserPrizesTable.prizeId eq prizeId)
        } > 0
    }

    private fun findUserId(username: String): Int? {
        return UsersTable
            .selectAll()
            .where { UsersTable.username eq username }
            .firstOrNull()
            ?.get(UsersTable.id)
            ?.value
    }
}
