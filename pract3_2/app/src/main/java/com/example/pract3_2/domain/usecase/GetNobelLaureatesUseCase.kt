package com.example.pract3_2.domain.usecase

import com.example.pract3_2.domain.model.NobelLaureate
import com.example.pract3_2.domain.repository.NobelRepository

class GetNobelLaureatesUseCase(
    private val repository: NobelRepository
) {

    suspend operator fun invoke(
        year: String?,
        category: String?
    ): List<NobelLaureate> {
        return repository.getLaureates(
            year = year,
            category = category
        )
    }
}