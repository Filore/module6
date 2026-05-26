package com.example.pract3_2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String = "",
    val category: NobelTextDto? = null,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class NobelTextDto(
    val en: String? = null,
    val se: String? = null,
    val no: String? = null
)

@Serializable
data class LaureateDto(
    val id: String = "",
    val knownName: NobelTextDto? = null,
    val fullName: NobelTextDto? = null,
    val motivation: NobelTextDto? = null,
    val birth: BirthDto? = null,
    val links: List<LinkDto> = emptyList()
)

@Serializable
data class BirthDto(
    val date: String? = null,
    val place: BirthPlaceDto? = null
)

@Serializable
data class BirthPlaceDto(
    val city: NobelTextDto? = null,
    val country: NobelTextDto? = null
)

@Serializable
data class LinkDto(
    val rel: String? = null,
    val href: String? = null,
    val action: String? = null,
    val types: String? = null
)