/**
 * RESPONSE DTOs
 * Models for data received from the backend. Field names are camelCase to match the
 * server's JSON exactly. Gson ignores Kotlin default values, so the server must send
 * every field. A missing field arrives as null or 0 and can crash the UI later.
 */
package com.example.mova.data.remote.model

/** One active program and the user's progress through it. */
data class ChallengeDto(
    val id: String,                 // used to build check-in and join URLs
    val title: String,
    val description: String,
    val daysCompleted: Int,
    val durationDays: Int,
    val progressPercentage: Int,    // 0 to 100, already computed by the server
    val completed: Boolean
)

/** Derived analytics for one period. Lists are aligned with chart buckets. */
data class AnalyticsDto(
    val userId: String,
    val period: String,
    val goalTitle: String,
    val targetCalories: Int,
    val burnedCalories: Int,
    val calorieTrend: List<Float>,      // one value per bucket, oldest first
    val trainingVolume: List<Float>,    // minutes per bucket, oldest first
    val muscleGroupDistribution: MuscleDistributionDto?  // nullable so a missing value shows an empty donut, not a crash
)

/** Percentages of training across the three muscle groups. */
data class MuscleDistributionDto(
    val core: Int,
    val upper: Int,
    val lower: Int
)

/** Everything the Rewards screen needs in one response. */
data class RewardDto(
    val streakDays: Int,
    val consistencyLevel: String, // percentage of the last 7 days with a workout
   val updatedPoints: Int =0,
    val claimableRewards: List<ClaimableRewardDto>,
    val milestoneBadges: List<MilestoneBadgeDto>
)

/** A bonus that can be claimed once per completed challenge. */
data class ClaimableRewardDto(
    val id: String,                     // the UserChallenge id, sent back when claiming
    val name: String,
    val description: String,
    val isClaimed: Boolean,
    val isUnlocked: Boolean = true,
    val iconUrl: String? = null
)

/** A trophy or achievement, shown greyed out until unlocked. */
data class MilestoneBadgeDto(
    val id: String? = null,
    val name: String,
    val description: String,
   val isClaimed: Boolean = false,
    val isUnlocked: Boolean,
    val iconUrl: String? = null
)
data class ProgramDto(
    val id: String,
    val title: String,
    val description: String,
    val durationDays: Int,
    val difficulty: String,
    val joined: Boolean,
    val completed: Boolean
)