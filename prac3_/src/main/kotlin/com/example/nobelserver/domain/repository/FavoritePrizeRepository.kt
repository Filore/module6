package com.example.nobelserver.domain.repository

import com.example.nobelserver.domain.model.NobelPrize

interface FavoritePrizeRepository {
    fun getFavoritePrizes(username: String): List<NobelPrize>
    fun addFavoritePrize(username: String, prizeId: Int): Boolean
    fun deleteFavoritePrize(username: String, prizeId: Int): Boolean
}
