package com.example.mova.data.models

import com.google.gson.annotations.SerializedName
import com.example.mova.data.remote.models.BadgeDto
/**
 * DTO represents the server response payload
 * received after a user attempts to claim a reward or badge.
 */
data class ClaimResponseDto (
    ///shows whether the reward claim was processed successfully.
    @SerializedName("success")
    val success: Boolean,
    ///Server message detailing the result after claim request
    @SerializedName("message")
    val message: String,
   ////Users updated balance after claiming reward.
    @SerializedName ("updatedPoints")
    val updatedPoints: Int =0,
   ///reflects the newly unlocked state of updated badges.
    @SerializedName("updatedBadges")
    val updatedBadges: List<BadgeDto>? = null
    )


