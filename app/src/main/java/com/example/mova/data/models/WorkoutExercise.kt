package com.example.mova.data.models

import com.google.gson.annotations.SerializedName

data class WorkoutExercise(
    @SerializedName("workoutExerciseId") val workoutExerciseId: String? = null,
    @SerializedName("workoutId") val workoutId: String = "",
    @SerializedName("exerciseId") val exerciseId: String = "",
    @SerializedName("orderNumber") val orderNumber: Int = 1,
    @SerializedName("sets") val sets: Int = 3,
    @SerializedName("reps") val reps: Int = 12,
    @SerializedName("restSeconds") val restSeconds: Int = 30
)