package com.example.nobelserver.data.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 64).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
    val role = varchar("role", 32)
}

object PrizesTable : IntIdTable("prizes") {
    val awardYear = integer("award_year")
    val category = varchar("category", 64)
    val fullName = varchar("full_name", 128)
    val motivation = text("motivation")
    val detailLink = varchar("detail_link", 512)

    init {
        uniqueIndex(awardYear, category)
    }
}

object LaureatesTable : IntIdTable("laureates") {
    val prizeId = reference("prize_id", PrizesTable)
    val fullName = varchar("full_name", 256)
    val portion = varchar("portion", 16)
    val motivation = text("motivation")
    val portraitUrl = varchar("portrait_url", 512)
}

object UserPrizesTable : IntIdTable("user_prizes") {
    val userId = reference("user_id", UsersTable)
    val prizeId = reference("prize_id", PrizesTable)
    val addedAt = datetime("added_at")

    init {
        uniqueIndex(userId, prizeId)
    }
}
