package com.example.mova.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mova.ui.screens.profile.ProfileScreen
import com.example.mova.ui.screens.profile.SettingsScreen
import com.example.mova.ui.screens.workout.WorkoutScreen
import com.example.mova.ui.screens.challenges.ChallengesScreen
import com.example.mova.ui.screens.challenges.RewardsScreen
import com.example.mova.ui.screens.challenges.AnalyticsScreen

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Challenges : Screen("challenges", Icons.Default.RunCircle, "Challenges")
    object Rewards : Screen("rewards", Icons.Default.Star, "Rewards")
    object Workout : Screen("workout", Icons.Default.FitnessCenter, "Workout")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
    object Analytics : Screen("analytics", Icons.Default.BarChart, "Analytics")
}

@Composable
fun MovaNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Workout.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Workout.route) {
            WorkoutScreen()
        }
        composable(Screen.Challenges.route) {
            ChallengesScreen()
        }
        composable(Screen.Rewards.route) {
            RewardsScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToEdit = { /* TODO: Navigate to Edit Profile */ }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }
    }
}