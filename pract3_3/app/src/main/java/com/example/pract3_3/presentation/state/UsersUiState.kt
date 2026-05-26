package com.example.pract3_3.presentation.state

import com.example.pract3_3.domain.model.User

sealed interface UsersUiState {

    data object Loading : UsersUiState

    data class Success(
        val users: List<User>
    ) : UsersUiState

    data class Error(
        val message: String
    ) : UsersUiState
}