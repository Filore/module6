package com.example.pract3_3.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract3_3.domain.usecase.LoginUseCase
import com.example.pract3_3.presentation.state.LoginUiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var username by mutableStateOf("emilys")
        private set

    var password by mutableStateOf("emilyspass")
        private set

    var state by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun onUsernameChange(value: String) {
        username = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun login() {
        viewModelScope.launch {
            state = LoginUiState.Loading

            try {
                loginUseCase(
                    username = username,
                    password = password
                )

                state = LoginUiState.Success
            } catch (e: Exception) {
                state = LoginUiState.Error(
                    message = when {
                        e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                            "Нет соединения с интернетом"

                        e.message?.contains("401", ignoreCase = true) == true ->
                            "Неверные данные для входа"

                        else ->
                            e.message ?: "Ошибка авторизации"
                    }
                )
            }
        }
    }

    fun resetState() {
        state = LoginUiState.Idle
    }
}