package com.example.mova.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.mova.R
import com.example.mova.ui.theme.*

/**
 * Sign-up screen — allows a new user to create a MOVA account.
 *
 * Flow:
 *  1. User enters username, email, password, confirm password
 *  2. Form is validated locally (validateSignUp)
 *  3. AuthViewModel.register() is called, which calls AuthRepository → POST /api/auth/register
 *  4. On success, onSignUpSuccess() is invoked to navigate into the app
 *
 * SSO alternative: Google / Apple / Biometric buttons below the form.
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    Log.d("RegisterScreen", "Register screen loaded")

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val uiState by viewModel.uiState.collectAsState()

    // Navigate on success
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Log.d("RegisterScreen", "Registration successful — navigating in")
            viewModel.resetState()
            onSignUpSuccess()
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
                text = "Create an account",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MovaBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter your email to sign up for this app",
                fontSize = 14.sp,
                color = MovaGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            MovTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "username...",
                isError = errors.containsKey("username"),
                errorMessage = errors["username"]
            )
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
            MovTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "confirm password",
                isPassword = true,
                isError = errors.containsKey("confirmPassword"),
                errorMessage = errors["confirmPassword"]
            )

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
                    Log.d("RegisterScreen", "Continue tapped")
                    val validation = validateSignUp(username, email, password, confirmPassword)
                    errors = validation
                    if (validation.isEmpty()) {
                        viewModel.register(username, email, password)
                    } else {
                        Log.w("RegisterScreen", "Validation errors: $validation")
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(22.dp))
                } else {
                    Text(text = "Continue", color = MaterialTheme.colorScheme.surface, fontSize = 16.sp)
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
                text = "Already have an account? Log in",
                fontSize = 13.sp,
                color = MovaBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onNavigateToLogin)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Validates the sign-up form. Returns map of field name → error message.
 * Empty map means valid.
 */
private fun validateSignUp(
    username: String,
    email: String,
    password: String,
    confirmPassword: String
): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (username.isBlank()) errors["username"] = "Username is required"
    else if (username.length < 3) errors["username"] = "Username must be at least 3 characters"

    if (email.isBlank()) errors["email"] = "Email is required"
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors["email"] = "Enter a valid email address"
    }

    if (password.isBlank()) errors["password"] = "Password is required"
    else if (password.length < 8) errors["password"] = "Password must be at least 8 characters"

    if (confirmPassword != password) errors["confirmPassword"] = "Passwords do not match"

    return errors
}