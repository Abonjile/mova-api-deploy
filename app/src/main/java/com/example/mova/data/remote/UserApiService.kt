package com.example.mova.data.remote

import com.example.mova.data.models.SettingsRequest
import com.example.mova.data.models.User
import retrofit2.http.*

interface UserApiService {

    @GET("api/users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: String
    ): User

    @PUT("api/users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body user: User
    ): User

    @PUT("api/users/{userId}/settings")
    suspend fun updateUserSettings(
        @Path("userId") userId: String,
        @Body settings: SettingsRequest
    ): User
}