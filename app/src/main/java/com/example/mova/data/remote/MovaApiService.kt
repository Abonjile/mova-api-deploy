/**
 * =====================================================================================
 * INTERFACE: MovaApiService
 * =====================================================================================
 * Retrofit definitions for every endpoint the app calls. Each path here must match a
 * route on the server exactly. A mismatch returns a 404, and the screen then shows
 * empty data with no obvious cause.
 *
 * Every function is a suspend function, so it runs on a coroutine and never blocks
 * the UI thread.
 * =====================================================================================
 */
package com.example.mova.data.remote

import com.example.mova.data.remote.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MovaApiService {

    /** Joined challenges for one user, with progress already calculated by the server. */
    @GET("api/challenges/user/{userId}")
    suspend fun getUserChallenges(
        @Path("userId") userId: String
    ): Response<List<ChallengeDto>>

    /** Adds the user to a challenge. Returns 409 if they already joined. */
    @POST("api/challenges/{challengeId}/join")
    suspend fun joinChallenge(
        @Path("challengeId") challengeId: String,
        @Body body: UserIdRequest
    ): Response<ResponseBody>

    /** Records today's workout toward a challenge. Returns 409 if already done today. */
    @POST("api/challenges/{challengeId}/checkin")
    suspend fun checkInChallenge(
        @Path("challengeId") challengeId: String,
        @Body body: UserIdRequest
    ): Response<ResponseBody>

    /** Analytics for a period. "period" is sent as a query string, e.g. ?period=Weekly. */
    @GET("api/analytics/{userId}")
    suspend fun getAnalytics(
        @Path("userId") userId: String,
        @Query("period") period: String
    ): Response<AnalyticsDto>

    /** Saves the goal for one period. The period is a query parameter so each period keeps its own goal. */
    @PUT("api/analytics/{userId}")
    suspend fun updateAnalyticsGoal(
        @Path("userId") userId: String,
        @Query("period") period: String,
        @Body body: GoalUpdateRequest
    ): Response<ResponseBody>

    /** Streak, consistency, claimable rewards and trophies for the Rewards screen. */
    @GET("api/rewards/{userId}")
    suspend fun getRewards(
        @Path("userId") userId: String
    ): Response<RewardDto>

    /** Claims one reward. The server rejects a second claim, so retrying is safe. */
    @POST("api/rewards/claim")
    suspend fun claimReward(
        @Body body: ClaimRewardRequest
    ): Response<ResponseBody>

    /** Every active program, marked joined or not for this user. */
    @GET("api/challenges/catalogue/{userId}")
    suspend fun getCatalogue(
        @Path("userId") userId: String
    ): Response<List<ProgramDto>>
}