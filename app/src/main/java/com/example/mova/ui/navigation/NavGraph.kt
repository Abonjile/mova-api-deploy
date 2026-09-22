package com.example.mova.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mova.ui.screens.challenges.AnalyticsScreen
import com.example.mova.ui.screens.challenges.ChallengesScreen
import com.example.mova.ui.screens.challenges.RewardsScreen
import com.example.mova.ui.screens.profile.ProfileScreen
import com.example.mova.ui.screens.workout.WorkoutDetailScreen
import com.example.mova.ui.screens.workout.WorkoutInProgressScreen
import com.example.mova.ui.screens.workout.WorkoutScreen
import com.example.mova.ui.screens.workout.WorkoutBuilderLoggerScreen

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Challenges : Screen("challenges", Icons.Default.RunCircle, "Challenges")
    object Rewards : Screen("rewards", Icons.Default.Star, "Rewards")
    object Workout : Screen("workout", Icons.Default.FitnessCenter, "Workout")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
    object Analytics : Screen("analytics", Icons.Default.BarChart, "Analytics")

    // Detail routes (not in bottom nav)
    object WorkoutDetail : Screen("workout_detail/{workoutId}", Icons.Default.Info, "Detail") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }
    object WorkoutInProgress : Screen("workout_in_progress/{workoutId}", Icons.Default.PlayArrow, "In Progress") {
        fun createRoute(workoutId: String) = "workout_in_progress/$workoutId"
    }

    object WorkoutBuilderLogger : Screen("workout_builder_logger", Icons.Default.Add, "Create")
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
            WorkoutScreen(
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                },
                onAddClick = {
                    navController.navigate(Screen.WorkoutBuilderLogger.route)
                }
            )
        }
        composable(Screen.WorkoutBuilderLogger.route) {
            WorkoutBuilderLoggerScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Challenges.route) {
            ChallengesScreen()
        }
        composable(Screen.Rewards.route) {
            RewardsScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }

        // Workout Detail route
        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            WorkoutDetailScreen(
                workoutId = workoutId,
                onBack = { navController.popBackStack() },
                onStartWorkout = { id ->
                    navController.navigate(Screen.WorkoutInProgress.createRoute(id))
                }
            )
        }

        // Workout In Progress route
        composable(
            route = Screen.WorkoutInProgress.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            WorkoutInProgressScreen(
                workoutId = workoutId,
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.popBackStack(Screen.Workout.route, inclusive = false)
                }
            )
        }
    }
}