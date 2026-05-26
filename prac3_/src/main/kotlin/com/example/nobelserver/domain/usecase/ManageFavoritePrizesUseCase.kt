package com.example.nobelserver.domain.usecase

import com.example.nobelserver.domain.model.NobelPrize
import com.example.nobelserver.domain.repository.FavoritePrizeRepository

class ManageFavoritePrizesUseCase(
    private val repository: FavoritePrizeRepository
) {
    fun getFavorites(username: String): List<NobelPrize> {
        return repository.getFavoritePrizes(username)
    }

    fun addFavorite(username: String, prizeId: Int): Boolean {
        return repository.addFavoritePrize(username, prizeId)
    }

    fun deleteFavorite(username: String, prizeId: Int): Boolean {
        return repository.deleteFavoritePrize(username, prizeId)
    }
}
