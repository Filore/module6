package com.example.pract3_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pract3_3.domain.usecase.GetUserByIdUseCase
import com.example.pract3_3.domain.usecase.LogoutUseCase

class UserDetailViewModelFactory(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserDetailViewModel(
                getUserByIdUseCase = getUserByIdUseCase,
                logoutUseCase = logoutUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}