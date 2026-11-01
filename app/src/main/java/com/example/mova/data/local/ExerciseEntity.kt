package com.example.mova.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Caches exercises locally so they load offline
@Entity(tableName = "cached_exercises")
data class ExerciseEntity(
    @PrimaryKey val exerciseId: String,
    val name: String,
    val description: String,
    val targetMuscleGroup: String,
    val difficulty: String,
    val videoUrl: String,
    val metValue: Double,
    val isActive: Boolean
)