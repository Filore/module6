package com.example.pract3_2.domain.usecase

import com.example.pract3_2.domain.repository.NobelRepository

class RemoveFavoritePrizeUseCase(
    private val repository: NobelRepository
) {

    suspend operator fun invoke(prizeId: String) {
        repository.removeFavoritePrize(prizeId)
    }
}
