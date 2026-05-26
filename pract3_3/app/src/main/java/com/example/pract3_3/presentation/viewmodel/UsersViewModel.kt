package com.example.pract3_3.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract3_3.domain.usecase.GetUsersUseCase
import com.example.pract3_3.presentation.state.UsersUiState
import kotlinx.coroutines.launch

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    var state by mutableStateOf<UsersUiState>(UsersUiState.Loading)
        private set

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            state = UsersUiState.Loading

            try {
                val users = getUsersUseCase()
                state = UsersUiState.Success(users)
            } catch (e: Exception) {
                state = UsersUiState.Error(
                    message = e.message ?: "Не удалось загрузить пользователей"
                )
            }
        }
    }
}