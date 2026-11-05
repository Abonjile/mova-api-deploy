package com.example.mova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey val workoutExerciseId: String,
    val workoutId: String,
    val exerciseId: String,
    val orderNumber: Int,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int
)