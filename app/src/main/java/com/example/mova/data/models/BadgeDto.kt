package com.example.mova.data.remote.models

import com.google.gson.annotations.SerializedName
import com.example.mova.R
/**
 * DTO represents an individual milestone badge or achievement
 * earned by the user within the MOVA gamification and rewards module.
 */
data class BadgeDto(

    /**
     * Unique string identifier for the badge.
     */
    @SerializedName("id")
    val id: String,

    /**
     * Display name of the achievement or milestone (e.g., "7-Day Champion").
     */
    @SerializedName("name")
    val name: String,

    /**
     * Detailed criteria required to unlock the badge.
     */
    @SerializedName("description")
    val description: String,

    /**
     * Flag indicating whether the user has met the requirements to unlock this badge.
     */
    @SerializedName("isUnlocked")
    val isUnlocked: Boolean = false,

    /**
     * Flag indicating whether the user has already claimed the reward points for this badge.
     */
    @SerializedName("isClaimed")
    val isClaimed: Boolean = false,

    /**
     * Optional icon graphic URL or resource descriptor for rendering badge visuals.
     */
    @SerializedName("iconUrl")
    val iconUrl: String? = null
)

fun BadgeDto.getBadgeDrawable(): Int {
    return when (iconUrl) {
        "ic_number_one_trophy" -> R.drawable.ic_number_one_trophy
        "ic_golden_medal" -> R.drawable.ic_golden_medal
        "ic_golden_trophy" -> R.drawable.ic_golden_trophy
        "ic_red_medal" -> R.drawable.ic_red_medal
        else -> R.drawable.ic_red_medal
    }
}
