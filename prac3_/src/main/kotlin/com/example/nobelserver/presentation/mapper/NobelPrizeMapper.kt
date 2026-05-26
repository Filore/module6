package com.example.nobelserver.presentation.mapper

import com.example.nobelserver.domain.model.Laureate
import com.example.nobelserver.domain.model.NobelPrize
import com.example.nobelserver.presentation.dto.LaureateResponse
import com.example.nobelserver.presentation.dto.NobelPrizeResponse

fun NobelPrize.toResponse(): NobelPrizeResponse {
    return NobelPrizeResponse(
        id = id,
        year = year,
        category = category,
        categoryFullName = categoryFullName,
        motivation = motivation,
        detailLink = detailLink,
        laureates = laureates.map { it.toResponse() }
    )
}

fun Laureate.toResponse(): LaureateResponse {
    return LaureateResponse(
        id = id,
        fullName = fullName,
        portion = portion,
        motivation = motivation,
        portraitUrl = portraitUrl
    )
}
