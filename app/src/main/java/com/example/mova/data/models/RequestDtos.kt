/**
 * REQUEST DTOs
 * Typed request bodies for the API. Using named classes instead of Map<String, Any>
 * gives compile-time checking of field names, so a typo fails the build rather than
 * sending a silently wrong request.
 * Gson uses these property names as the JSON keys, so they must match the server's
 * expected field names exactly.
 * */
package com.example.mova.data.remote.model

/** Body for join and check-in requests. */
data class UserIdRequest(val userId: String)

/** Body for PUT /api/analytics. Both fields are sent together to keep the goal consistent. */
data class GoalUpdateRequest(val goalTitle: String, val targetCalories: Int)

/** Body for POST /api/rewards/claim. */
data class ClaimRewardRequest(val userId: String, val rewardId: String)