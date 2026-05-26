package com.example.nobelserver.data.repository

import com.example.nobelserver.data.database.LaureatesTable
import com.example.nobelserver.data.database.PrizesTable
import com.example.nobelserver.domain.model.Laureate
import com.example.nobelserver.domain.model.NobelPrize
import com.example.nobelserver.domain.repository.NobelPrizeRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresNobelPrizeRepository : NobelPrizeRepository {
    override fun getAllPrizes(): List<NobelPrize> = transaction {
        PrizesTable
            .selectAll()
            .orderBy(PrizesTable.awardYear, SortOrder.DESC)
            .map { it.toPrize() }
    }

    override fun getPrizeById(id: Int): NobelPrize? = transaction {
        PrizesTable
            .selectAll()
            .where { PrizesTable.id eq id }
            .firstOrNull()
            ?.toPrize()
    }

    override fun getPrizeByYearAndCategory(year: Int, category: String): NobelPrize? = transaction {
        PrizesTable
            .selectAll()
            .where {
                (PrizesTable.awardYear eq year) and
                    (PrizesTable.category eq category.lowercase())
            }
            .firstOrNull()
            ?.toPrize()
    }

    override fun getLaureatesByPrize(year: Int, category: String): List<Laureate>? = transaction {
        val prize = PrizesTable
            .selectAll()
            .where {
                (PrizesTable.awardYear eq year) and
                    (PrizesTable.category eq category.lowercase())
            }
            .firstOrNull()
            ?: return@transaction null

        laureatesFor(prize[PrizesTable.id].value)
    }

    private fun ResultRow.toPrize(): NobelPrize {
        val prizeId = this[PrizesTable.id].value
        return NobelPrize(
            id = prizeId,
            year = this[PrizesTable.awardYear],
            category = this[PrizesTable.category],
            categoryFullName = this[PrizesTable.fullName],
            motivation = this[PrizesTable.motivation],
            detailLink = this[PrizesTable.detailLink],
            laureates = laureatesFor(prizeId)
        )
    }

    private fun laureatesFor(prizeId: Int): List<Laureate> {
        return LaureatesTable
            .selectAll()
            .where { LaureatesTable.prizeId eq prizeId }
            .map { row ->
                Laureate(
                    id = row[LaureatesTable.id].value,
                    fullName = row[LaureatesTable.fullName],
                    portion = row[LaureatesTable.portion],
                    motivation = row[LaureatesTable.motivation],
                    portraitUrl = row[LaureatesTable.portraitUrl]
                )
            }
    }
}
