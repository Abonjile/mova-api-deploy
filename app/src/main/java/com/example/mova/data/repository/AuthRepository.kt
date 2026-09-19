package com.example.mova.data.repository

import android.util.Log
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
     * TODO: Implement when Google Sign-In SDK is added.
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthResponse> {
        Log.d(TAG, "Google SSO not implemented yet (token length=${idToken.length})")
        return Result.failure(NotImplementedError("Google SSO not implemented yet"))
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