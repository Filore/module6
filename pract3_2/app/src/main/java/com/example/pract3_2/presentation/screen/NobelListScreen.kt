package com.example.pract3_2.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pract3_2.domain.model.NobelLaureate
import com.example.pract3_2.presentation.state.NobelUiState

@Composable
fun NobelListScreen(
    state: NobelUiState,
    year: String,
    category: String,
    onYearChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onRetryClick: () -> Unit,
    favoritesMode: Boolean,
    favoritePrizeIds: Set<String>,
    onAllClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onFavoriteClick: (NobelLaureate) -> Unit,
    onLaureateClick: (NobelLaureate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FilterBlock(
            year = year,
            category = category,
            onYearChange = onYearChange,
            onCategoryChange = onCategoryChange,
            onSearchClick = onSearchClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModeBlock(
            favoritesMode = favoritesMode,
            onAllClick = onAllClick,
            onFavoritesClick = onFavoritesClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            NobelUiState.Loading -> {
                LoadingContent()
            }

            is NobelUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetryClick = onRetryClick
                )
            }

            is NobelUiState.Success -> {
                LaureatesList(
                    laureates = state.laureates,
                    favoritePrizeIds = favoritePrizeIds,
                    onFavoriteClick = onFavoriteClick,
                    onLaureateClick = onLaureateClick
                )
            }
        }
    }
}

@Composable
private fun ModeBlock(
    favoritesMode: Boolean,
    onAllClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (favoritesMode) {
            OutlinedButton(
                onClick = onAllClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Все премии")
            }

            Button(
                onClick = onFavoritesClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Избранное")
            }
        } else {
            Button(
                onClick = onAllClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Все премии")
            }

            OutlinedButton(
                onClick = onFavoritesClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Избранное")
            }
        }
    }
}

@Composable
private fun FilterBlock(
    year: String,
    category: String,
    onYearChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    OutlinedTextField(
        value = year,
        onValueChange = onYearChange,
        label = { Text("Год, например 2023") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    CategoryDropdown(
        selectedCategory = category,
        onCategorySelected = onCategoryChange
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onSearchClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Фильтровать")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "",
        "phy",
        "che",
        "med",
        "lit",
        "pea",
        "eco"
    )

    val categoryLabels = mapOf(
        "" to "Все категории",
        "phy" to "Physics",
        "che" to "Chemistry",
        "med" to "Medicine",
        "lit" to "Literature",
        "pea" to "Peace",
        "eco" to "Economics"
    )

    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = categoryLabels[selectedCategory] ?: "Все категории",
            onValueChange = {},
            readOnly = true,
            label = { Text("Категория") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(categoryLabels[category] ?: category)
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onRetryClick) {
            Text("Повторить")
        }
    }
}

@Composable
private fun LaureatesList(
    laureates: List<NobelLaureate>,
    favoritePrizeIds: Set<String>,
    onFavoriteClick: (NobelLaureate) -> Unit,
    onLaureateClick: (NobelLaureate) -> Unit
) {
    if (laureates.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Лауреаты не найдены")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(laureates) { laureate ->
                LaureateCard(
                    laureate = laureate,
                    isFavorite = favoritePrizeIds.contains(laureate.prizeId),
                    onFavoriteClick = {
                        onFavoriteClick(laureate)
                    },
                    onClick = {
                        onLaureateClick(laureate)
                    }
                )
            }
        }
    }
}

@Composable
private fun LaureateCard(
    laureate: NobelLaureate,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "${laureate.year} — ${laureate.category}",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = laureate.fullName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = laureate.motivation.take(100),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onFavoriteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFavorite) {
                        "Удалить из избранного"
                    } else {
                        "Добавить в избранное"
                    }
                )
            }
        }
    }
}
