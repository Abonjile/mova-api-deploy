package com.example.mova.ui.screens.authentication

import android.util.Log
import android.app.Activity
import android.content.ContextWrapper
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.compose.ui.platform.LocalContext
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.mova.ui.theme.*
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.mova.utils.BiometricHelper

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

    // Credential Manager Setup
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()

    val webClientId = "919226330565-0o5mq0sbug1btm74q7rde3rvnr54934r.apps.googleusercontent.com"

    val activity = context.findActivity() as? FragmentActivity

    val launchBiometric: () -> Unit = {
        if (activity != null && BiometricHelper.canAuthenticate(context)) {
            BiometricHelper.showPrompt(
                activity = activity,
                onSuccess = {
                    viewModel.signInWithBiometric()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Biometric authentication not set up on this device.", Toast.LENGTH_LONG).show()
        }
    }

    val launchGoogleSignIn: () -> Unit = {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                // Explicitly check for GoogleIdTokenCredential or CustomCredential
                if (credential is GoogleIdTokenCredential) {
                    Log.d("AuthScreen", "Google ID Token received: ${credential.idToken.take(10)}...")
                    viewModel.signInWithGoogle(credential.idToken)
                } else if (credential is androidx.credentials.CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Fallback extraction for older/newer versions of Play Services
                    val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.d("AuthScreen", "Google ID Token extracted from CustomCredential")
                    viewModel.signInWithGoogle(googleCred.idToken)
                } else {
                    Log.e("AuthScreen", "Unexpected credential type: ${credential::class.java.name}")
                }
            } catch (e: Exception) {
                // Catch generic exception to see what's actually failing
                Log.e("AuthScreen", "Credential Manager Error: ${e.message}", e)
            }
        }
    }

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
            .background(MaterialTheme.colorScheme.background)
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
                color = MaterialTheme.colorScheme.onBackground,
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
                    color = MaterialTheme.colorScheme.onBackground,
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
                    color = MaterialTheme.colorScheme.error,
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))

            SocialLoginRow(
                onGoogleClick = launchGoogleSignIn,
                onAppleClick = { viewModel.signInWithApple() },
                onBiometricClick = launchBiometric
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

// Safely find the Activity from the Context
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}