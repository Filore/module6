package com.example.pract3_2.domain.repository

import com.example.pract3_2.domain.model.NobelLaureate

interface NobelRepository {

    suspend fun login(
        username: String,
        password: String
    )

    suspend fun getLaureates(
        year: String?,
        category: String?
    ): List<NobelLaureate>

    suspend fun getFavoriteLaureates(): List<NobelLaureate>

    suspend fun addFavoritePrize(prizeId: String)

    suspend fun removeFavoritePrize(prizeId: String)
}
