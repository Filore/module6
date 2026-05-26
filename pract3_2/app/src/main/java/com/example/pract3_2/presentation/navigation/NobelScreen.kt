package com.example.pract3_2.presentation.navigation

sealed class NobelScreen(
    val route: String
) {
    data object Login : NobelScreen("login")

    data object List : NobelScreen("nobel_list")

    data object Detail : NobelScreen("nobel_detail/{laureateId}") {
        fun createRoute(laureateId: String): String {
            return "nobel_detail/$laureateId"
        }
    }
}
