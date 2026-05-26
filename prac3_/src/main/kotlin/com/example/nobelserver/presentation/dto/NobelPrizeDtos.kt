package com.example.nobelserver.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeResponse(
    val id: Int,
    val year: Int,
    val category: String,
    val categoryFullName: String,
    val motivation: String,
    val detailLink: String,
    val laureates: List<LaureateResponse>
)

@Serializable
data class LaureateResponse(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val portraitUrl: String
)
