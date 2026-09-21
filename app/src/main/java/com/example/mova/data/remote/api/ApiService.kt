package com.example.mova.data.remote.api

import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.models.WorkoutLogRequest
import retrofit2.http.*

interface ApiService {

    // ─── EXERCISES ───
    @GET("api/exercises")
    suspend fun getAllExercises(): List<Exercise>

    @GET("api/exercises/{id}")
    suspend fun getExerciseById(@Path("id") exerciseId: String): Exercise

    // ─── WORKOUTS ───
    @GET("api/workouts")
    suspend fun getWorkoutsByUser(@Query("userId") userId: String): List<Workout>

    @GET("api/workouts/{id}")
    suspend fun getWorkoutById(@Path("id") workoutId: String): Workout

    @POST("api/workouts")
    suspend fun createWorkout(@Body workout: Workout): Workout

    // ─── WORKOUT LOGGING ───
    @POST("api/workouts/log")
    suspend fun logWorkout(@Body request: WorkoutLogRequest): WorkoutLogResponse

    companion object {
        // 10.0.2.2 = localhost from Android emulator (BlueStacks may differ)
        const val BASE_URL = "http://10.0.2.2:3000/"
    }
}

// Request body for logging a workout
data class WorkoutLogRequest(
    val userId: String,
    val workoutId: String,
    val durationMinutes: Int,
    val weightAtTime: Double,
    val userNote: String = ""
)

// Response from logging a workout
data class WorkoutLogResponse(
    val message: String,
    val calculatedCalories: Int = 0
)