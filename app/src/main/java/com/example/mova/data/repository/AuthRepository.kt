package com.example.mova.data.repository

import android.util.Log
import com.example.mova.data.local.MovaDatabase
import com.example.mova.data.models.AuthRequest
import com.example.mova.data.models.AuthResponse
import com.example.mova.data.models.User
import com.example.mova.data.models.toDomain
import com.example.mova.data.models.toEntity
import com.example.mova.data.remote.RetrofitClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles authentication with the backend REST API with offline Room DB fallback.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val database: MovaDatabase
) {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val userDao = database.userDao()
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

            response.user?.let { u ->
                userDao.insertUser(u.toEntity().copy(isSynced = true))
            }

            Log.d(TAG, "Register success: ${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Register API failed, saving user locally offline: ${e.message}")
            val offlineUserId = "usr_${System.currentTimeMillis()}"
            val offlineUser = User(
                userId = offlineUserId,
                fullName = fullName,
                email = email
            )
            userDao.insertUser(offlineUser.toEntity().copy(isSynced = false))
            Result.success(
                AuthResponse(
                    message = "Registered in offline mode",
                    user = offlineUser
                )
            )
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

            response.user?.let { u ->
                userDao.insertUser(u.toEntity().copy(isSynced = true))
            }

            Log.d(TAG, "Login success: ${response.message}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Login API failed, checking local database: ${e.message}")
            val localUser = userDao.getUserByEmail(email) ?: userDao.getAnyUser()
            if (localUser != null) {
                Log.d(TAG, "Offline login successful for ${localUser.fullName}")
                Result.success(
                    AuthResponse(
                        message = "Logged in offline",
                        user = localUser.toDomain()
                    )
                )
            } else {
                Result.failure(
                    Exception("Offline mode: No account found for $email on this device. Connect to the internet to log in.")
                )
            }
        }
    }

    /**
     * Sign in with a Google ID token (SSO).
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthResponse> {
        Log.d(TAG, "Google SSO not implemented yet (token length=${idToken.length})")
        return Result.failure(NotImplementedError("Google SSO not implemented yet"))
    }

    /**
     * Sign in with an Apple identity token (SSO).
     */
    suspend fun signInWithApple(identityToken: String): Result<AuthResponse> {
        Log.d(TAG, "Apple SSO not implemented yet (token length=${identityToken.length})")
        return Result.failure(NotImplementedError("Apple SSO not implemented yet"))
    }
}