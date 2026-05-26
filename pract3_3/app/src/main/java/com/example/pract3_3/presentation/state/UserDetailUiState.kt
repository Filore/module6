package com.example.pract3_3.presentation.state

import com.example.pract3_3.domain.model.User

sealed interface UserDetailUiState {

    data object Loading : UserDetailUiState

    data class Success(
        val user: User
    ) : UserDetailUiState

    data class Error(
        val message: String
    ) : UserDetailUiState
}