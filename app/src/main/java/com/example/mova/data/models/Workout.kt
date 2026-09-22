package com.example.mova.data.models

import com.google.gson.annotations.SerializedName

data class Workout(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("workoutId") val workoutId: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("difficulty") val difficulty: String = "BEGINNER",
    @SerializedName("durationMinutes") val durationMinutes: Int = 20,
    @SerializedName("isCustom") val isCustom: Boolean = false,
    @SerializedName("icon") val icon: String = "fitness",
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("exercises") val exercises: List<WorkoutExercise> = emptyList()
) {
    val displayIcon: String
        get() = if (description.contains("||icon:")) description.substringAfter("||icon:") else icon

    val displayDescription: String
        get() = if (description.contains("||icon:")) description.substringBefore("||icon:") else description
}