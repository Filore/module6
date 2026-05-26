package com.example.pract3_3.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pract3_3.domain.usecase.GetUsersUseCase

class UsersViewModelFactory(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(getUsersUseCase) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}