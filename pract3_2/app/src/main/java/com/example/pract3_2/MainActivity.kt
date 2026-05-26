package com.example.pract3_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.pract3_2.di.AppModule
import com.example.pract3_2.presentation.navigation.NobelNavGraph
import com.example.pract3_2.presentation.viewmodel.NobelViewModel
import com.example.pract3_2.presentation.viewmodel.NobelViewModelFactory
import com.example.pract3_2.ui.theme.Pract3_2Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = NobelViewModelFactory(
            getNobelLaureatesUseCase = AppModule.getNobelLaureatesUseCase,
            loginUseCase = AppModule.loginUseCase,
            getFavoriteNobelLaureatesUseCase = AppModule.getFavoriteNobelLaureatesUseCase,
            addFavoritePrizeUseCase = AppModule.addFavoritePrizeUseCase,
            removeFavoritePrizeUseCase = AppModule.removeFavoritePrizeUseCase
        )

        val viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[NobelViewModel::class.java]

        setContent {
            Pract3_2Theme {
                NobelNavGraph(
                    viewModel = viewModel
                )
            }
        }
    }
}
