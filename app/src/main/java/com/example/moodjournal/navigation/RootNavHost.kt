package com.example.moodjournal.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodjournal.presentation.checkin.CheckInScreen
import com.example.moodjournal.presentation.splash.SplashScreen
import com.example.moodjournal.presentation.timeline.TimelineScreen

@Composable
fun RootNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = NavRoutes.Splash.route,
            exitTransition = { fadeOut() }
        ) {
            SplashScreen(
                navigateToCheckIn = {
                    navController.navigate(NavRoutes.CheckIn.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.CheckIn.route,
            enterTransition = { fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
        ) {
            CheckInScreen(
                onEntrySaved = { entryId ->
                    navController.navigate(NavRoutes.Timeline.route) {
                        popUpTo(NavRoutes.CheckIn.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.Timeline.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            TimelineScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
