package com.example.mova.data.repository

import android.content.Context
import androidx.room.Room
import com.example.mova.data.local.MovaDatabase
import com.example.mova.data.local.WorkoutEntity
import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.models.WorkoutExercise
import com.example.mova.data.models.WorkoutLog
import com.example.mova.data.remote.api.RetrofitClient
import com.example.mova.data.remote.api.WorkoutLogRequest
import com.example.mova.data.remote.api.WorkoutLogResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WorkoutRepository(context: Context? = null) {

    private val api = RetrofitClient.apiService

    // Setup Room Database
    private val db = if (context != null) {
        Room.databaseBuilder(
            context.applicationContext,
            MovaDatabase::class.java,
            "mova_database"
        ).fallbackToDestructiveMigration().build()
    } else null

    private val workoutDao = db?.workoutDao()

    // ─── EXERCISES ───
    suspend fun getAllExercises(): List<Exercise> {
        return try {
            api.getAllExercises()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ─── WORKOUTS ───
    suspend fun getSystemWorkouts(): List<Workout> {
        return try {
            val apiWorkouts = api.getWorkoutsByUser("system")
            // Cache them locally
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            // Offline fallback: Load system / non-custom workouts from RoomDB
            val cached = workoutDao?.getAllWorkouts() ?: emptyList()
            val systemList = cached.filter { it.userId == "system" || (!it.isCustom && it.userId.isEmpty()) }
                .map { it.toDomain() }
            systemList
        }
    }

    suspend fun getUserWorkouts(userId: String): List<Workout> {
        return try {
            val apiWorkouts = api.getWorkoutsByUser(userId)
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            // Offline fallback: Load from RoomDB
            val cached = workoutDao?.getAllWorkouts() ?: emptyList()
            cached.filter { it.userId == userId }.map { it.toDomain() }
        }
    }

    suspend fun getCustomWorkouts(userId: String): List<Workout> {
        return try {
            val apiWorkouts = api.getWorkoutsByUser(userId, isCustom = true)
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            // Offline fallback
            val cached = workoutDao?.getAllWorkouts() ?: emptyList()
            cached.filter { it.userId == userId && it.isCustom }.map { it.toDomain() }
        }
    }

    suspend fun getWorkoutById(workoutId: String): Workout {
        return try {
            val apiWorkout = api.getWorkoutById(workoutId)
            workoutDao?.insertWorkouts(listOf(apiWorkout.toEntity()))
            apiWorkout
        } catch (e: Exception) {
            val cached = workoutDao?.getAllWorkouts()?.firstOrNull { it.workoutId == workoutId }
            cached?.toDomain() ?: Workout(workoutId = workoutId, name = "Workout")
        }
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

    suspend fun getWorkoutLogs(userId: String): List<WorkoutLog> {
        return try {
            api.getWorkoutLogs(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// Mappers
private val gson = Gson()

fun Workout.toEntity(): WorkoutEntity {
    val exercisesString = try {
        gson.toJson(exercises)
    } catch (_: Exception) {
        "[]"
    }
    return WorkoutEntity(
        workoutId = workoutId,
        userId = userId,
        name = name,
        description = description,
        difficulty = difficulty,
        durationMinutes = durationMinutes,
        isCustom = isCustom,
        iconName = icon ?: "fitness",
        exercisesJson = exercisesString
    )
}

fun WorkoutEntity.toDomain(): Workout {
    val exerciseList = try {
        val type = object : TypeToken<List<WorkoutExercise>>() {}.type
        gson.fromJson<List<WorkoutExercise>>(exercisesJson, type) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
    return Workout(
        workoutId = workoutId,
        userId = userId,
        name = name,
        description = description,
        difficulty = difficulty,
        durationMinutes = durationMinutes,
        isCustom = isCustom,
        icon = iconName,
        exercises = exerciseList
    )
}