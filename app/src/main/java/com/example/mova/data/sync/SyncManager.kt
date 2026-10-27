package com.example.mova.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.mova.data.local.MovaDatabase
import com.example.mova.data.remote.api.RetrofitClient
import com.example.mova.data.remote.api.WorkoutLogRequest

class SyncManager(
    private val context: Context,
    private val database: MovaDatabase
) {
    private val api = RetrofitClient.apiService

    // Check if the phone currently has internet
    fun isOnline(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    // Loop through all unsynced logs and push them to Render
    suspend fun syncPendingLogs() {
        // Don't try if we are offline
        if (!isOnline()) return

        val pendingLogs = database.offlineWorkoutLogDao().getUnsyncedLogs()

        for (log in pendingLogs) {
            try {
                // Push to the backend
                api.logWorkout(
                    WorkoutLogRequest(
                        userId = log.userId,
                        workoutId = log.workoutId,
                        durationMinutes = log.durationMinutes,
                        weightAtTime = log.weightAtTime,
                        userNote = log.userNote
                    )
                )
                // If successful, mark it as synced so we don't send it again
                database.offlineWorkoutLogDao().markAsSynced(log.id)
            } catch (e: Exception) {
                // If it fails (e.g. Render is asleep), break and try again later
                break
            }
        }
    }
}