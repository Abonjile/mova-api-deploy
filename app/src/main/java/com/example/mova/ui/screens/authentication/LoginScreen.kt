package com.example.mova.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.mova.ui.theme.*

/**
 * Login screen — allows a registered user to sign in.
 *
 * Flow:
 *  1. User enters email + password
 *  2. Form is validated locally
 *  3. AuthViewModel.login() → AuthRepository → POST /api/auth/login
 *  4. On success, onLoginSuccess() navigates into the app
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    Log.d("LoginScreen", "Login screen loaded")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Log.d("LoginScreen", "Login successful — navigating in")
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MovaOffWhite)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBannerSection()

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Login to track your workouts and achieve your fitness goals",
                fontSize = 14.sp,
                color = MovaBlack,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            MovTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "email@domain.com",
                keyboardType = KeyboardType.Email,
                isError = errors.containsKey("email"),
                errorMessage = errors["email"]
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "password...",
                isPassword = true,
                isError = errors.containsKey("password"),
                errorMessage = errors["password"]
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "forgot password?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MovaBlack,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        Log.d("LoginScreen", "Forgot password tapped")
                        // TODO: navigate to password reset flow
                    }
                )
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "❌ ${uiState.error}",
                    color = MovaError,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    Log.d("LoginScreen", "Continue tapped")
                    val validation = validateLogin(email, password)
                    errors = validation
                    if (validation.isEmpty()) {
                        viewModel.login(email, password)
                    } else {
                        Log.w("LoginScreen", "Validation errors: $validation")
                    }
                },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MovaBlueLight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text(text = "Continue", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Or Continue with :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MovaBlack
            )
            Spacer(modifier = Modifier.height(20.dp))

            SocialLoginRow(
                onGoogleClick = { viewModel.signInWithGoogle() },
                onAppleClick = { viewModel.signInWithApple() },
                onBiometricClick = { viewModel.signInWithBiometric() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Don't have an account? Sign up",
                fontSize = 13.sp,
                color = MovaBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onNavigateToSignUp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Validates login form.
 */
private fun validateLogin(email: String, password: String): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (email.isBlank()) errors["email"] = "Email is required"
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors["email"] = "Enter a valid email address"
    }

    if (password.isBlank()) errors["password"] = "Password is required"

    return errors
}