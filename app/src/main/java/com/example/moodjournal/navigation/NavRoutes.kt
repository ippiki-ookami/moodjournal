package com.example.moodjournal.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object CheckIn : NavRoutes("check_in")
    data object Timeline : NavRoutes("timeline")
}
