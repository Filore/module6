package com.example.pract3_2.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pract3_2.domain.model.NobelLaureate

@Composable
fun NobelDetailScreen(
    laureate: NobelLaureate,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!laureate.portraitUrl.isNullOrBlank()) {
            AsyncImage(
                model = laureate.portraitUrl,
                contentDescription = laureate.fullName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = laureate.fullName,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Год: ${laureate.year}"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Категория: ${laureate.category}"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Страна: ${laureate.birthCountry ?: "Не указано"}"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Место рождения: ${laureate.birthPlace ?: "Не указано"}"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Описание:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = laureate.motivation
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isFavorite) {
                    OutlinedButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить из избранного")
                    }
                } else {
                    Button(
                        onClick = onFavoriteClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить в избранное")
                    }
                }
            }
        }
    }
}
