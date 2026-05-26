package com.example.pract3_2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pract3_2.domain.usecase.AddFavoritePrizeUseCase
import com.example.pract3_2.domain.usecase.GetFavoriteNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.GetNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.LoginUseCase
import com.example.pract3_2.domain.usecase.RemoveFavoritePrizeUseCase

class NobelViewModelFactory(
    private val getNobelLaureatesUseCase: GetNobelLaureatesUseCase,
    private val loginUseCase: LoginUseCase,
    private val getFavoriteNobelLaureatesUseCase: GetFavoriteNobelLaureatesUseCase,
    private val addFavoritePrizeUseCase: AddFavoritePrizeUseCase,
    private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NobelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NobelViewModel(
                getNobelLaureatesUseCase = getNobelLaureatesUseCase,
                loginUseCase = loginUseCase,
                getFavoriteNobelLaureatesUseCase = getFavoriteNobelLaureatesUseCase,
                addFavoritePrizeUseCase = addFavoritePrizeUseCase,
                removeFavoritePrizeUseCase = removeFavoritePrizeUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
