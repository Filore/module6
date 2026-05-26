package com.example.pract3_2.data.repository

import com.example.pract3_2.data.mapper.toLaureates
import com.example.pract3_2.data.remote.api.NobelApi
import com.example.pract3_2.domain.model.NobelLaureate
import com.example.pract3_2.domain.repository.NobelRepository

class NobelRepositoryImpl(
    private val api: NobelApi
) : NobelRepository {

    override suspend fun login(
        username: String,
        password: String
    ) {
        api.login(username, password)
    }

    override suspend fun getLaureates(
        year: String?,
        category: String?
    ): List<NobelLaureate> {
        return api.getNobelPrizes(
            year = year,
            category = category
        ).toLaureates()
    }

    override suspend fun getFavoriteLaureates(): List<NobelLaureate> {
        return api.getFavoritePrizes().toLaureates()
    }

    override suspend fun addFavoritePrize(prizeId: String) {
        api.addFavoritePrize(prizeId)
    }

    override suspend fun removeFavoritePrize(prizeId: String) {
        api.removeFavoritePrize(prizeId)
    }
}
