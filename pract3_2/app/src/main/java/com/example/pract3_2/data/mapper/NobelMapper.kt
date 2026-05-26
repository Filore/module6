package com.example.pract3_2.data.mapper

import com.example.pract3_2.domain.model.NobelLaureate
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun JsonElement.toLaureates(): List<NobelLaureate> {
    return prizeElements().flatMap { prize ->
        prize.toLaureates()
    }
}

private fun JsonElement.prizeElements(): List<JsonObject> {
    return when (this) {
        is JsonArray -> mapNotNull { it as? JsonObject }
        is JsonObject -> {
            val prizes = this["nobelPrizes"]
                ?: this["prizes"]
                ?: this["favoritePrizes"]
                ?: this["favorites"]
                ?: this["data"]
                ?: this["items"]
                ?: this["results"]

            when (prizes) {
                is JsonArray -> prizes.mapNotNull { it as? JsonObject }
                is JsonObject -> listOf(prizes)
                else -> listOf(this)
            }
        }
        else -> emptyList()
    }
}

private fun JsonObject.toLaureates(): List<NobelLaureate> {
    val prizeId = stringValue("id", "prizeId") ?: ""
    val awardYear = textValue("awardYear", "award_year", "year")
        ?: "Год не указан"
    val category = textValue("category", "nobelPrizeCategory", "categoryName")
        ?: "Unknown"
    val prizeMotivation = textValue("motivation", "description")
        ?: "Описание отсутствует"
    val prizeFullName = textValue("fullName", "full_name", "name", "title")
        ?: category
    val prizeLink = stringValue("detailLink", "detail_link", "href", "url")
    val laureates = this["laureates"] as? JsonArray

    if (laureates.isNullOrEmpty()) {
        return listOf(
            NobelLaureate(
                id = prizeId.ifBlank { "$awardYear-$category-$prizeFullName" },
                prizeId = prizeId,
                fullName = prizeFullName,
                year = awardYear,
                category = category,
                motivation = prizeMotivation,
                birthCountry = textValue("birthCountry", "birth_country", "country"),
                birthPlace = textValue("birthPlace", "birth_place", "city"),
                portraitUrl = stringValue("portraitUrl", "portrait_url", "imageUrl", "image_url")
                    ?: prizeLink
            )
        )
    }

    return laureates.mapNotNull { it as? JsonObject }.map { laureate ->
        val laureateId = laureate.stringValue("id", "laureateId", "laureate_id")
            ?: prizeId
        val fullName = laureate.textValue("fullName", "full_name", "knownName", "name")
            ?: prizeFullName
        val motivation = laureate.textValue("motivation")
            ?: prizeMotivation

        NobelLaureate(
            id = listOf(prizeId, laureateId, fullName)
                .filter { it.isNotBlank() }
                .joinToString("-")
                .ifBlank { "$awardYear-$category-$fullName" },
            prizeId = prizeId,
            fullName = fullName,
            year = awardYear,
            category = category,
            motivation = motivation,
            birthCountry = laureate.textValue("birthCountry", "birth_country", "country")
                ?: laureate.nestedTextValue("birth", "place", "country"),
            birthPlace = laureate.textValue("birthPlace", "birth_place", "city")
                ?: laureate.nestedTextValue("birth", "place", "city"),
            portraitUrl = laureate.stringValue("portraitUrl", "portrait_url", "imageUrl", "image_url")
        )
    }
}

private fun JsonObject.textValue(vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key ->
        this[key]?.asText()
    }
}

private fun JsonObject.stringValue(vararg keys: String): String? {
    return textValue(*keys)
}

private fun JsonObject.nestedTextValue(
    firstKey: String,
    secondKey: String,
    thirdKey: String
): String? {
    val first = this[firstKey] as? JsonObject ?: return null
    val second = first[secondKey] as? JsonObject ?: return null
    return second[thirdKey]?.asText()
}

private fun JsonElement.asText(): String? {
    return when (this) {
        is JsonPrimitive -> contentOrNull?.takeIf { it.isNotBlank() }
        is JsonObject -> listOf("en", "ru", "value", "name", "title", "text")
            .firstNotNullOfOrNull { key ->
                this[key]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
            }
        else -> null
    }
}
