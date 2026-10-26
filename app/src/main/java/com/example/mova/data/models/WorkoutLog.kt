package com.example.mova.data.models

import com.google.gson.annotations.SerializedName

data class WorkoutLog(
    @SerializedName("workoutLogId") val workoutLogId: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("workoutId") val workoutId: String = "",
    @SerializedName("durationMinutes") val durationMinutes: Int = 0,
    @SerializedName("caloriesBurned") val caloriesBurned: Float = 0f,
    @SerializedName("completed") val completed: Boolean = true,
    @SerializedName("userNote") val userNote: String = "",
    @SerializedName("createdAt") val createdAt: String = ""
)