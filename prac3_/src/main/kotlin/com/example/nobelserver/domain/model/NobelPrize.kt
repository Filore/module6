package com.example.nobelserver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val id: Int,
    val year: Int,
    val category: String,
    val categoryFullName: String,
    val motivation: String,
    val detailLink: String,
    val laureates: List<Laureate>
)
