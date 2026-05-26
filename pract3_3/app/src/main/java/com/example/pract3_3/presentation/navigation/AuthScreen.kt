package com.example.pract3_3.presentation.navigation

sealed class AuthScreen(
    val route: String
) {
    data object Login : AuthScreen("login")

    data object UsersList : AuthScreen("users_list")

    data object UserDetail : AuthScreen("user_detail/{userId}") {
        fun createRoute(userId: Int): String {
            return "user_detail/$userId"
        }
    }
}