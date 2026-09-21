package com.example.mova.data.remote

import com.example.mova.data.remote.model.AnalyticsDto
import com.example.mova.data.remote.model.ChallengeDto
import com.example.mova.data.remote.model.RewardDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service interface for communicating with the MOVA backend server.
 * Handles endpoints for challenges, analytics, and rewards.
 */
interface MovaApiService {

    /**
     * Fetches the list of active fitness challenge programs for the authenticated user.
     *
     * @return A Response object containing a list of [ChallengeDto] items.
     */
    @GET("api/challenges/active")
    suspend fun getActiveChallenges(): Response<List<ChallengeDto>>

    /**
     * Fetches performance analytics and calorie trend metrics filtered by period.
     *
     * @param period The timeframe filter, e.g., "Weekly" or "Monthly".
     * @return A Response object containing the [AnalyticsDto] data.
     */
    @GET("api/analytics")
    suspend fun getAnalytics(
        @Query("period") period: String
    ): Response<AnalyticsDto>

    /**
     * Retrieves reward milestone details and streak data for the user profile.
     *
     * @return A Response object containing the [RewardDto] details.
     */
    @GET("api/rewards/user/{userId}")
    suspend fun getUserRewards(
        @Path("userId") userId: String
    ): Response<RewardDto>
}