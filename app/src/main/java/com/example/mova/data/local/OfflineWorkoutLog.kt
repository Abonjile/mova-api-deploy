package com.example.mova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// This entity stores workout logs created while the user is offline.
@Entity(tableName = "offline_workout_logs")
data class OfflineWorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val workoutId: String,
    val durationMinutes: Int,
    val weightAtTime: Double,
    val userNote: String,
    val isSynced: Boolean = false
)