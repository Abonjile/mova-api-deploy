package com.example.mova.data.repository

import android.util.Log
import com.example.mova.data.local.MovaDatabase
import com.example.mova.data.local.UserDao
import com.example.mova.data.models.User
import com.example.mova.data.models.toDomain
import com.example.mova.data.models.toEntity
import com.example.mova.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user data with offline-first strategy using local database as source of truth with remote API fallback and offline sync support
 * */
@Singleton
class UserRepository @Inject constructor(
    private val database: MovaDatabase
) {

    private val userDao = database.userDao()
    private val userApiService = RetrofitClient.userApiService

    companion object {
        private const val TAG = "UserRepository"
    }

    /**
     * Get user from local database (offline-first)
     * If not found locally, fetch from API
     * Return mock data before backend is ready
     */
    suspend fun getUser(userId: String): User? {
        Log.d(TAG, "Getting user: $userId")

        // 1. Try to get from local database first
        val localUser = userDao.getUserById(userId)
        if (localUser != null) {
            Log.d(TAG, "User found locally: ${localUser.fullName}")
            return localUser.toDomain()
        }

        // 2. If not found locally, fetch from API
        try {
            Log.d(TAG, "User not found locally, fetching from API")
            val remoteUser = userApiService.getUserProfile(userId)
            Log.d(TAG, "User fetched from API: ${remoteUser.fullName}")

            // 3. Save to local database
            userDao.insertUser(remoteUser.toEntity())
            Log.d(TAG, "User saved to local database")

            return remoteUser
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user from API: ${e.message}")
            Log.d(TAG, "Returning mock user for UI development")

            // Return mock data for this UI phase
            val mockUser = User(
                userId = userId,
                fullName = "Katlego Mokwena",
                email = "katlego@example.com",
                location = "Johannesburg, SA",
                height = 178.0,
                weight = 76.0,
                age = 26,
                gender = "Male",
                fitnessLevel = "Intermed.",
                fitnessGoal = "Burn 2000 cal weekly"
            )

            // Save mock user to loacl DB so it persists
            userDao.insertUser(mockUser.toEntity())

            return mockUser
        }
    }

    /**
     * Observe user from local database as Flow (for real-time updates)
     */
    fun observeUser(userId: String): Flow<User?> {
        return userDao.getUserByIdFlow(userId).map { entity ->
            entity?.toDomain()
        }
    }

    /**
     * Update user profile (saves locally and syncs with API)
     */
    suspend fun updateUserProfile(userId: String, user: User): Result<User> {
        return try {
            Log.d(TAG, "Updating user: ${user.fullName}")

            // 1. Update in API
            val updatedUser = userApiService.updateUserProfile(userId, user)
            Log.d(TAG, "User updated on API")

            // 2. Update in local database
            userDao.insertUser(updatedUser.toEntity().copy(isSynced = true))
            Log.d(TAG, "User updated in local database")

            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}")

            // 3. Save locally with sync flag = false for later sync
            userDao.insertUser(user.toEntity().copy(isSynced = false))
            Log.d(TAG, "User saved locally (pending sync)")

            // Return success with local data UI phase
            Result.success(user)
        }
    }

    /**
     * Update user settings
     */
    suspend fun updateSettings(
        userId: String,
        language: String,
        notificationsEnabled: Boolean,
        biometricEnabled: Boolean
    ): Result<User> {
        return try {
            Log.d(TAG, "Updating settings for user: $userId")

            val settings = mapOf(
                "language" to language,
                "notificationsEnabled" to notificationsEnabled,
                "biometricEnabled" to biometricEnabled
            )

            val updatedUser = userApiService.updateUserSettings(userId, settings)
            Log.d(TAG, "Settings updated on API")

            userDao.insertUser(updatedUser.toEntity().copy(isSynced = true))
            Log.d(TAG, "Settings updated in local database")

            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating settings: ${e.message}")

            // Return success with local data for UI phase
            val currentUser = userDao.getUserById(userId)?.toDomain() ?: User(userId = userId)
            val updatedUser = currentUser.copy(
                language = language,
                notificationsEnabled = notificationsEnabled,
                biometricEnabled = biometricEnabled
            )
            userDao.insertUser(updatedUser.toEntity().copy(isSynced = true))

            Result.success(updatedUser)
        }
    }

    /**
     * Sync offline data with server
     */
    suspend fun syncOfflineData(userId: String) {
        try {
            Log.d(TAG, "Syncing offline data...")

            val unsyncedUsers = userDao.getUnsyncedUsers()
            for (user in unsyncedUsers) {
                try {
                    userApiService.updateUserProfile(userId, user.toDomain())
                    userDao.updateSyncStatus(user.userId, true)
                    Log.d(TAG, "Synced user: ${user.fullName}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync user: ${user.fullName} - ${e.message}")
                }
            }

            Log.d(TAG, "Sync complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sync: ${e.message}")
        }
    }
}