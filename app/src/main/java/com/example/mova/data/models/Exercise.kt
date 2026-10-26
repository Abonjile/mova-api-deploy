package com.example.mova.data.models

import com.google.gson.annotations.SerializedName

data class Exercise(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("exerciseId") val exerciseId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("targetMuscleGroup") val targetMuscleGroup: String = "",
    @SerializedName("difficulty") val difficulty: String = "BEGINNER",
    @SerializedName("videoUrl") val videoUrl: String = "",
    @SerializedName("metValue") val metValue: Double = 3.8,
    @SerializedName("defaultSets") val defaultSets: Int = 3,
    @SerializedName("defaultReps") val defaultReps: Int = 12,
    @SerializedName("isActive") val isActive: Boolean = true
)