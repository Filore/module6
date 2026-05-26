package com.example.pract3_2.domain.usecase

import com.example.pract3_2.domain.model.NobelLaureate
import com.example.pract3_2.domain.repository.NobelRepository

class GetFavoriteNobelLaureatesUseCase(
    private val repository: NobelRepository
) {

    suspend operator fun invoke(): List<NobelLaureate> {
        return repository.getFavoriteLaureates()
    }
}
