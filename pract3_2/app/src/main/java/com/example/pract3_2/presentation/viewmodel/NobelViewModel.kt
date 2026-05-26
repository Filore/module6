package com.example.pract3_2.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract3_2.domain.model.NobelLaureate
import com.example.pract3_2.domain.usecase.AddFavoritePrizeUseCase
import com.example.pract3_2.domain.usecase.GetFavoriteNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.GetNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.LoginUseCase
import com.example.pract3_2.domain.usecase.RemoveFavoritePrizeUseCase
import com.example.pract3_2.presentation.state.NobelUiState
import kotlinx.coroutines.launch

class NobelViewModel(
    private val getNobelLaureatesUseCase: GetNobelLaureatesUseCase,
    private val loginUseCase: LoginUseCase,
    private val getFavoriteNobelLaureatesUseCase: GetFavoriteNobelLaureatesUseCase,
    private val addFavoritePrizeUseCase: AddFavoritePrizeUseCase,
    private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
) : ViewModel() {

    var state by mutableStateOf<NobelUiState>(NobelUiState.Loading)
        private set

    var year by mutableStateOf("")
        private set

    var category by mutableStateOf("")
        private set

    var favoritePrizeIds by mutableStateOf<Set<String>>(emptySet())
        private set

    var favoritesMode by mutableStateOf(false)
        private set

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    var isLoginLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    fun onYearChange(value: String) {
        year = value.filter { it.isDigit() }.take(4)
    }

    fun onCategoryChange(value: String) {
        category = value
    }

    fun onUsernameChange(value: String) {
        username = value
        loginError = null
    }

    fun onPasswordChange(value: String) {
        password = value
        loginError = null
    }

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            loginError = "Введите логин и пароль"
            return
        }

        viewModelScope.launch {
            isLoginLoading = true
            loginError = null

            try {
                loginUseCase(
                    username = username.trim(),
                    password = password
                )
                isLoggedIn = true
                loadLaureates()
            } catch (e: Exception) {
                loginError = e.message ?: "Не удалось войти"
            } finally {
                isLoginLoading = false
            }
        }
    }

    fun loadLaureates() {
        viewModelScope.launch {
            favoritesMode = false
            state = NobelUiState.Loading

            try {
                val result = getNobelLaureatesUseCase(
                    year = year.takeIf { it.isNotBlank() },
                    category = category.takeIf { it.isNotBlank() }
                )

                refreshFavoritePrizeIds()
                state = NobelUiState.Success(result)
            } catch (e: Exception) {
                state = NobelUiState.Error(
                    message = e.message ?: "Не удалось загрузить лауреатов"
                )
            }
        }
    }

    fun loadFavoriteLaureates() {
        viewModelScope.launch {
            favoritesMode = true
            state = NobelUiState.Loading

            try {
                val result = getFavoriteNobelLaureatesUseCase()
                favoritePrizeIds = result.prizeIds()
                state = NobelUiState.Success(result)
            } catch (e: Exception) {
                state = NobelUiState.Error(
                    message = e.message ?: "Не удалось загрузить избранное"
                )
            }
        }
    }

    fun toggleFavorite(laureate: NobelLaureate) {
        if (laureate.prizeId.isBlank()) {
            state = NobelUiState.Error("У этой премии нет идентификатора для избранного")
            return
        }

        viewModelScope.launch {
            try {
                if (favoritePrizeIds.contains(laureate.prizeId)) {
                    removeFavoritePrizeUseCase(laureate.prizeId)
                    favoritePrizeIds = favoritePrizeIds - laureate.prizeId
                    removeFromCurrentFavorites(laureate.prizeId)
                } else {
                    addFavoritePrizeUseCase(laureate.prizeId)
                    favoritePrizeIds = favoritePrizeIds + laureate.prizeId
                }
            } catch (e: Exception) {
                state = NobelUiState.Error(
                    message = e.message ?: "Не удалось обновить избранное"
                )
            }
        }
    }

    private fun removeFromCurrentFavorites(prizeId: String) {
        val currentState = state
        if (favoritesMode && currentState is NobelUiState.Success) {
            state = NobelUiState.Success(
                currentState.laureates.filterNot { it.prizeId == prizeId }
            )
        }
    }

    private suspend fun refreshFavoritePrizeIds() {
        runCatching {
            getFavoriteNobelLaureatesUseCase().prizeIds()
        }.onSuccess { prizeIds ->
            favoritePrizeIds = prizeIds
        }
    }

    private fun List<NobelLaureate>.prizeIds(): Set<String> {
        return mapNotNull { laureate ->
            laureate.prizeId.takeIf { it.isNotBlank() }
        }.toSet()
    }
}
