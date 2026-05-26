package com.example.pract3_2.domain.model

data class NobelLaureate(
    val id: String,
    val prizeId: String,
    val fullName: String,
    val year: String,
    val category: String,
    val motivation: String,
    val birthCountry: String?,
    val birthPlace: String?,
    val portraitUrl: String?
)
