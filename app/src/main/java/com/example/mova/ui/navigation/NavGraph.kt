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
import com.example.mova.ui.screens.SplashGate
import com.example.mova.ui.screens.onboarding.OnboardingScreen
import com.example.mova.ui.screens.authentication.RegisterScreen
import com.example.mova.ui.screens.authentication.LoginScreen
import com.example.mova.ui.screens.profile.ProfileScreen
import com.example.mova.ui.screens.profile.EditProfileScreen
import com.example.mova.ui.screens.profile.SettingsScreen
import com.example.mova.ui.screens.workout.WorkoutScreen
import com.example.mova.ui.screens.workout.WorkoutDetailScreen
import com.example.mova.ui.screens.workout.WorkoutInProgressScreen
import com.example.mova.ui.screens.workout.WorkoutBuilderLoggerScreen
import com.example.mova.ui.screens.challenges.ChallengesScreen
import com.example.mova.ui.screens.rewards.RewardsScreen
import com.example.mova.ui.screens.analytics.AnalyticsScreen

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Challenges : Screen("challenges", Icons.Default.RunCircle, "Challenges")
    object Rewards : Screen("rewards", Icons.Default.Star, "Rewards")
    object Workout : Screen("workout", Icons.Default.FitnessCenter, "Workout")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
    object Analytics : Screen("analytics", Icons.Default.BarChart, "Analytics")

    // Workout detail routes (not in bottom nav)
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
    startDestination: String = "splash"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ===== Splash / Session gate =====
        composable("splash") {
            SplashGate(
                onRouteDecided = { route ->
                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // ===== Auth =====
        composable("signup") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("signup") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Workout.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Workout.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ===== Workout =====
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

        // ===== Other bottom-nav screens =====
        composable(Screen.Challenges.route) {
            ChallengesScreen()
        }

        composable(Screen.Rewards.route) {
            RewardsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToEdit = { navController.navigate(route = "edit_profile") },
                onLoggedOut = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onBackPressed = { navController.popBackStack() }
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