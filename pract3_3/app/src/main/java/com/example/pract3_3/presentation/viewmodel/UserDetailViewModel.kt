package com.example.pract3_3.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract3_3.domain.usecase.GetUserByIdUseCase
import com.example.pract3_3.domain.usecase.LogoutUseCase
import com.example.pract3_3.presentation.state.UserDetailUiState
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    var state by mutableStateOf<UserDetailUiState>(UserDetailUiState.Loading)
        private set

    fun loadUser(id: Int) {
        viewModelScope.launch {
            state = UserDetailUiState.Loading

            try {
                val user = getUserByIdUseCase(id)
                state = UserDetailUiState.Success(user)
            } catch (e: Exception) {
                state = UserDetailUiState.Error(
                    message = e.message ?: "Не удалось загрузить пользователя"
                )
            }
        }
    }

    fun logout(
        onLoggedOut: () -> Unit
    ) {
        viewModelScope.launch {
            logoutUseCase()
            onLoggedOut()
        }
    }
}