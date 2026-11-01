package com.example.mova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches workouts locally so the dashboard still loads offline
@Entity(tableName = "cached_workouts")
data class WorkoutEntity(
    @PrimaryKey val workoutId: String,
    val userId: String,
    val name: String,
    val description: String,
    val difficulty: String,
    val durationMinutes: Int,
    val isCustom: Boolean,
    val iconName: String,
    val exercisesJson: String = "[]"
)