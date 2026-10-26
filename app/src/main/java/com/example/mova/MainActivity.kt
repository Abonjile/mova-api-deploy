package com.example.mova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mova.ui.navigation.MovaNavGraph
import com.example.mova.ui.navigation.Screen
import com.example.mova.ui.theme.MovaTheme
import com.example.mova.data.session.SessionManager
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observes dark mode preference from DataStore
            val darkMode by sessionManager.darkMode.collectAsState()

            MovaTheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MovaApp()
                }
            }
        }
    }
}

@Composable
fun MovaApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "splash"

    // Screens where the bottom nav bar should be hidden
    val authRoutes = setOf("splash", "signup", "login", "onboarding")
    val showBottomBar = currentRoute !in authRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val screens = listOf(
                        Screen.Challenges,
                        Screen.Rewards,
                        Screen.Workout,
                        Screen.Profile,
                        Screen.Analytics
                    )

                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        MovaNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}