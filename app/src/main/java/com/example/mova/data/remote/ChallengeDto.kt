package com.example.mova.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing a fitness challenge program retrieved from the backend API.
 */
data class ChallengeDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("current_day") val currentDay: Int,
    @SerializedName("total_days") val totalDays: Int,
    @SerializedName("completion_percentage") val completionPercentage: String
)

/**
 * Data Transfer Object representing user analytics and calorie metrics for weekly or monthly periods.
 */
data class AnalyticsDto(
    @SerializedName("period") val period: String, // "Weekly" or "Monthly"
    @SerializedName("target_calories") val targetCalories: Int,
    @SerializedName("burned_calories") val burnedCalories: Int,
    @SerializedName("calorie_trend") val calorieTrend: List<Float>,
    @SerializedName("training_volume") val trainingVolume: List<Float>
)

/**
 * Data Transfer Object representing reward milestones and active streaks for the user.
 */
data class RewardDto(
    @SerializedName("streak_days") val streakDays: Int,
    @SerializedName("badges_earned") val badgesEarned: Int,
    @SerializedName("reward_title") val rewardTitle: String,
    @SerializedName("is_claimed") val isClaimed: Boolean
)