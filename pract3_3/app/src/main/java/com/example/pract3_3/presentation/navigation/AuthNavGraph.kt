package com.example.pract3_3.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pract3_3.di.AppModule
import com.example.pract3_3.presentation.screen.LoginScreen
import com.example.pract3_3.presentation.screen.UserDetailScreen
import com.example.pract3_3.presentation.screen.UsersListScreen
import com.example.pract3_3.presentation.viewmodel.LoginViewModel
import com.example.pract3_3.presentation.viewmodel.LoginViewModelFactory
import com.example.pract3_3.presentation.viewmodel.UserDetailViewModel
import com.example.pract3_3.presentation.viewmodel.UserDetailViewModelFactory
import com.example.pract3_3.presentation.viewmodel.UsersViewModel
import com.example.pract3_3.presentation.viewmodel.UsersViewModelFactory

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val owner = LocalViewModelStoreOwner.current!!

    val context =
        androidx.compose.ui.platform.LocalContext.current.applicationContext

    val loginViewModel = ViewModelProvider(
        owner,
        LoginViewModelFactory(
            AppModule.provideLoginUseCase(context)
        )
    )[LoginViewModel::class.java]

    val usersViewModel = ViewModelProvider(
        owner,
        UsersViewModelFactory(
            AppModule.provideGetUsersUseCase(context)
        )
    )[UsersViewModel::class.java]

    val userDetailViewModel = ViewModelProvider(
        owner,
        UserDetailViewModelFactory(
            getUserByIdUseCase = AppModule.provideGetUserByIdUseCase(context),
            logoutUseCase = AppModule.provideLogoutUseCase(context)
        )
    )[UserDetailViewModel::class.java]

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        composable(AuthScreen.Login.route) {
            LoginScreen(
                username = loginViewModel.username,
                password = loginViewModel.password,
                state = loginViewModel.state,
                onUsernameChange = loginViewModel::onUsernameChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                onLoginClick = loginViewModel::login,
                onLoginSuccess = {
                    loginViewModel.resetState()
                    usersViewModel.loadUsers()

                    navController.navigate(AuthScreen.UsersList.route) {
                        popUpTo(AuthScreen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AuthScreen.UsersList.route) {
            UsersListScreen(
                state = usersViewModel.state,
                onUserClick = { user ->
                    navController.navigate(
                        AuthScreen.UserDetail.createRoute(user.id)
                    )
                },
                onRetryClick = usersViewModel::loadUsers
            )
        }

        composable(
            route = AuthScreen.UserDetail.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val userId =
                backStackEntry.arguments?.getInt("userId") ?: 0

            UserDetailScreen(
                userId = userId,
                state = userDetailViewModel.state,
                onLoadUser = userDetailViewModel::loadUser,
                onLogoutClick = {
                    userDetailViewModel.logout {
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(0)
                        }
                    }
                },
                onRetryClick = {
                    userDetailViewModel.loadUser(userId)
                }
            )
        }
    }
}