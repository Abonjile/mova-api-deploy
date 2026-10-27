package com.example.mova.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.example.mova.data.models.AuthRequest
import com.example.mova.data.models.AuthResponse
import com.example.mova.data.remote.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles authentication with the backend REST API.
 *
 * Responsibilities:
 *  - POST /api/auth/register with email + password
 *  - POST /api/auth/login with email + password
 *  - (Future) SSO via provider tokens
 *
 * Returns Result<T> so the ViewModel can handle success/failure
 * without try-catch duplication.
 */
@Singleton
class AuthRepository @Inject constructor() {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val api = RetrofitClient.authApiService

    /**
     * Register a new account with username + email + password.
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Registering new user: $email")

            val response = api.register(
                AuthRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )

            Log.d(TAG, "Register success: ${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Register failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Login with email + password.
     */
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Logging in: $email")

            val response = api.login(
                AuthRequest(email = email, password = password)
            )

            Log.d(TAG, "Login success: ${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Sign in with a Google ID token (SSO).
     */
    suspend fun signInWithGoogleToBackend(idToken: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Authenticating with Firebase...")

            // 1. Authenticate with Firebase
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()

            val firebaseUser = authResult.user ?: throw Exception("Firebase user is null")
            val email = firebaseUser.email ?: throw Exception("Email is null")
            val fullName = firebaseUser.displayName ?: "Mova User"
            val uid = firebaseUser.uid // App uses Firebase UID as the SSO ID

            Log.d(TAG, "Firebase Auth Success: $email")

            // 2. Try to Log in to the Backend first
            try {
                val loginRequest = AuthRequest(
                    email = email,
                    ssoProvider = "GOOGLE",
                    ssoProviderId = uid
                )
                // If this succeeds, it returns the AuthResponse directly
                val loginResponse = api.login(loginRequest)
                Log.d(TAG, "Backend Login Success: ${loginResponse.message}")
                return Result.success(loginResponse)

            } catch (loginException: Exception) {
                // If login fails, app will try registering
                Log.d(TAG, "Backend Login failed (likely new user). Registering...")

                val registerRequest = AuthRequest(
                    fullName = fullName,
                    email = email,
                    ssoProvider = "GOOGLE",
                    ssoProviderId = uid
                )

                // If this fails, it will throw an exception caught by the outer try-catch
                val registerResponse = api.register(registerRequest)
                Log.d(TAG, "Backend Register Success: ${registerResponse.message}")
                Result.success(registerResponse)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Google SSO failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Sign in with an Apple identity token (SSO).
     * TODO: Implement when Apple Sign-In is wired up.
     */
    suspend fun signInWithApple(identityToken: String): Result<AuthResponse> {
        Log.d(TAG, "Apple SSO not implemented yet (token length=${identityToken.length})")
        return Result.failure(NotImplementedError("Apple SSO not implemented yet"))
    }
}