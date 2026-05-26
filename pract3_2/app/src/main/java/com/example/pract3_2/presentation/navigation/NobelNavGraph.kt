package com.example.pract3_2.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pract3_2.presentation.screen.LoginScreen
import com.example.pract3_2.presentation.screen.NobelDetailScreen
import com.example.pract3_2.presentation.screen.NobelListScreen
import com.example.pract3_2.presentation.state.NobelUiState
import com.example.pract3_2.presentation.viewmodel.NobelViewModel

@Composable
fun NobelNavGraph(
    viewModel: NobelViewModel
) {
    val navController = rememberNavController()
    val state = viewModel.state

    NavHost(
        navController = navController,
        startDestination = NobelScreen.Login.route
    ) {
        composable(NobelScreen.Login.route) {
            LaunchedEffect(viewModel.isLoggedIn) {
                if (viewModel.isLoggedIn) {
                    navController.navigate(NobelScreen.List.route) {
                        popUpTo(NobelScreen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            }

            LoginScreen(
                username = viewModel.username,
                password = viewModel.password,
                isLoading = viewModel.isLoginLoading,
                error = viewModel.loginError,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::login
            )
        }

        composable(NobelScreen.List.route) {
            NobelListScreen(
                state = state,
                year = viewModel.year,
                category = viewModel.category,
                onYearChange = viewModel::onYearChange,
                onCategoryChange = viewModel::onCategoryChange,
                onSearchClick = viewModel::loadLaureates,
                onRetryClick = viewModel::loadLaureates,
                favoritesMode = viewModel.favoritesMode,
                favoritePrizeIds = viewModel.favoritePrizeIds,
                onAllClick = viewModel::loadLaureates,
                onFavoritesClick = viewModel::loadFavoriteLaureates,
                onFavoriteClick = viewModel::toggleFavorite,
                onLaureateClick = { laureate ->
                    navController.navigate(
                        NobelScreen.Detail.createRoute(laureate.id)
                    )
                }
            )
        }

        composable(
            route = NobelScreen.Detail.route,
            arguments = listOf(
                navArgument("laureateId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val laureateId =
                backStackEntry.arguments?.getString("laureateId")

            val laureate = when (state) {
                is NobelUiState.Success -> {
                    state.laureates.find { it.id == laureateId }
                }

                else -> null
            }

            if (laureate != null) {
                NobelDetailScreen(
                    laureate = laureate,
                    isFavorite = viewModel.favoritePrizeIds.contains(laureate.prizeId),
                    onFavoriteClick = {
                        viewModel.toggleFavorite(laureate)
                    }
                )
            }
        }
    }
}
