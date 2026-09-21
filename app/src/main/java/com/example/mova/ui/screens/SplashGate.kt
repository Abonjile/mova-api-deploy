package com.example.mova.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mova.data.session.SessionManager
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    suspend fun getRoute(): String {
        val loggedIn = sessionManager.isLoggedIn()
        val onboarded = sessionManager.isOnboardingComplete()
        return when {
            !loggedIn -> "signup"
            loggedIn && !onboarded -> "onboarding"
            else -> "workout"
        }
    }
}

/**
 * Decides whether to send the user into the app or to the auth flow.
 * Shows a spinner while DataStore is being read.
 */
@Composable
fun SplashGate(
    viewModel: SplashViewModel = hiltViewModel(),
    onRouteDecided: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        val route = viewModel.getRoute()
        Log.d("SplashGate", "Session check -> route=$route")
        onRouteDecided(route)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}