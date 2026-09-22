package com.example.mova.data.repository

import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.models.WorkoutExercise
import com.example.mova.data.remote.api.RetrofitClient
import com.example.mova.data.remote.api.WorkoutLogRequest
import com.example.mova.data.remote.api.WorkoutLogResponse

class WorkoutRepository {

    private val api = RetrofitClient.apiService

    // ─── EXERCISES ───
    suspend fun getAllExercises(): List<Exercise> {
        return api.getAllExercises()
    }

    // ─── WORKOUTS ───
    suspend fun getSystemWorkouts(): List<Workout> {
        return api.getWorkoutsByUser("system")
    }

    suspend fun getUserWorkouts(userId: String): List<Workout> {
        return api.getWorkoutsByUser(userId)
    }

    suspend fun getWorkoutById(workoutId: String): Workout {
        return api.getWorkoutById(workoutId)
    }

    suspend fun createWorkout(workout: Workout): Workout {
        return api.createWorkout(workout)
    }

    suspend fun createWorkoutExercises(workoutId: String, exercises: List<WorkoutExercise>): List<WorkoutExercise> {
        return api.createWorkoutExercises(workoutId, exercises)
    }

    // ─── LOGGING ───
    suspend fun logWorkout(request: WorkoutLogRequest): WorkoutLogResponse {
        return api.logWorkout(request)
    }
}