package com.example.nobelserver.domain.usecase

import com.example.nobelserver.domain.model.Laureate
import com.example.nobelserver.domain.model.NobelPrize
import com.example.nobelserver.domain.repository.NobelPrizeRepository

class GetNobelPrizesUseCase(
    private val repository: NobelPrizeRepository
) {
    fun getAll(): List<NobelPrize> = repository.getAllPrizes()

    fun getById(id: Int): NobelPrize? = repository.getPrizeById(id)

    fun getByYearAndCategory(year: Int, category: String): NobelPrize? {
        return repository.getPrizeByYearAndCategory(year, category)
    }

    fun getLaureates(year: Int, category: String): List<Laureate>? {
        return repository.getLaureatesByPrize(year, category)
    }
}
