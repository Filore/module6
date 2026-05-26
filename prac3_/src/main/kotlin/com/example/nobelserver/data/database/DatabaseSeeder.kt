package com.example.nobelserver.data.database

import com.example.nobelserver.security.PasswordHasher
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSeeder {
    fun seed() {
        transaction {
            seedUsers()
            seedPrizes()
        }
    }

    private fun seedUsers() {
        val usersCount = UsersTable.selectAll().count()
        if (usersCount > 0) return

        UsersTable.insert {
            it[username] = "admin"
            it[passwordHash] = PasswordHasher.hash("password")
            it[role] = "admin"
        }
        UsersTable.insert {
            it[username] = "student"
            it[passwordHash] = PasswordHasher.hash("qwerty123")
            it[role] = "user"
        }
    }

    private fun seedPrizes() {
        if (PrizesTable.selectAll().count() > 0) return

        samplePrizes.forEach { sample ->
            val prizeId = PrizesTable.insert {
                it[awardYear] = sample.year
                it[category] = sample.category
                it[fullName] = sample.fullName
                it[motivation] = sample.motivation
                it[detailLink] = sample.detailLink
            } get PrizesTable.id

            sample.laureates.forEach { laureate ->
                LaureatesTable.insert {
                    it[LaureatesTable.prizeId] = prizeId
                    it[fullName] = laureate.fullName
                    it[portion] = laureate.portion
                    it[motivation] = laureate.motivation
                    it[portraitUrl] = laureate.portraitUrl
                }
            }
        }
    }

    private val samplePrizes = listOf(
        SeedPrize(
            year = 2023,
            category = "physics",
            fullName = "Physics",
            motivation = "For experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter",
            detailLink = "https://www.nobelprize.org/prizes/physics/2023/summary/",
            laureates = listOf(
                SeedLaureate("Pierre Agostini", "1/3", "For experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                SeedLaureate("Ferenc Krausz", "1/3", "For experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                SeedLaureate("Anne L'Huillier", "1/3", "For experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter")
            )
        ),
        SeedPrize(
            year = 2023,
            category = "chemistry",
            fullName = "Chemistry",
            motivation = "For the discovery and synthesis of quantum dots",
            detailLink = "https://www.nobelprize.org/prizes/chemistry/2023/summary/",
            laureates = listOf(
                SeedLaureate("Moungi G. Bawendi", "1/3", "For the discovery and synthesis of quantum dots"),
                SeedLaureate("Louis E. Brus", "1/3", "For the discovery and synthesis of quantum dots"),
                SeedLaureate("Aleksey Yekimov", "1/3", "For the discovery and synthesis of quantum dots")
            )
        ),
        SeedPrize(
            year = 2023,
            category = "literature",
            fullName = "Literature",
            motivation = "For his innovative plays and prose which give voice to the unsayable",
            detailLink = "https://www.nobelprize.org/prizes/literature/2023/summary/",
            laureates = listOf(
                SeedLaureate("Jon Fosse", "1", "For his innovative plays and prose which give voice to the unsayable")
            )
        ),
        SeedPrize(
            year = 2023,
            category = "peace",
            fullName = "Peace",
            motivation = "For her fight against the oppression of women in Iran and her fight to promote human rights and freedom for all",
            detailLink = "https://www.nobelprize.org/prizes/peace/2023/summary/",
            laureates = listOf(
                SeedLaureate("Narges Mohammadi", "1", "For her fight against the oppression of women in Iran and her fight to promote human rights and freedom for all")
            )
        ),
        SeedPrize(
            year = 2022,
            category = "medicine",
            fullName = "Physiology or Medicine",
            motivation = "For his discoveries concerning the genomes of extinct hominins and human evolution",
            detailLink = "https://www.nobelprize.org/prizes/medicine/2022/summary/",
            laureates = listOf(
                SeedLaureate("Svante Paabo", "1", "For his discoveries concerning the genomes of extinct hominins and human evolution")
            )
        ),
        SeedPrize(
            year = 2022,
            category = "economics",
            fullName = "Economic Sciences",
            motivation = "For research on banks and financial crises",
            detailLink = "https://www.nobelprize.org/prizes/economic-sciences/2022/summary/",
            laureates = listOf(
                SeedLaureate("Ben S. Bernanke", "1/3", "For research on banks and financial crises"),
                SeedLaureate("Douglas W. Diamond", "1/3", "For research on banks and financial crises"),
                SeedLaureate("Philip H. Dybvig", "1/3", "For research on banks and financial crises")
            )
        )
    )

    private data class SeedPrize(
        val year: Int,
        val category: String,
        val fullName: String,
        val motivation: String,
        val detailLink: String,
        val laureates: List<SeedLaureate>
    )

    private data class SeedLaureate(
        val fullName: String,
        val portion: String,
        val motivation: String,
        val portraitUrl: String = ""
    )
}
