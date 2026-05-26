package com.example.pract3_2.presentation.state

import com.example.pract3_2.domain.model.NobelLaureate

sealed interface NobelUiState {

    data object Loading : NobelUiState

    data class Success(
        val laureates: List<NobelLaureate>
    ) : NobelUiState

    data class Error(
        val message: String
    ) : NobelUiState
}