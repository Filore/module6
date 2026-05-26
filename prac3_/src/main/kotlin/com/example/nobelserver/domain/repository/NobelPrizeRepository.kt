package com.example.nobelserver.domain.repository

import com.example.nobelserver.domain.model.Laureate
import com.example.nobelserver.domain.model.NobelPrize

interface NobelPrizeRepository {
    fun getAllPrizes(): List<NobelPrize>
    fun getPrizeById(id: Int): NobelPrize?
    fun getPrizeByYearAndCategory(year: Int, category: String): NobelPrize?
    fun getLaureatesByPrize(year: Int, category: String): List<Laureate>?
}
