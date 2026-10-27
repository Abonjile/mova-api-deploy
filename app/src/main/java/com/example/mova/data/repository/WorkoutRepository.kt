package com.example.mova.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.mova.data.local.*
import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.models.WorkoutExercise
import com.example.mova.data.models.WorkoutLog
import com.example.mova.data.remote.api.RetrofitClient
import com.example.mova.data.remote.api.WorkoutLogRequest
import com.example.mova.data.remote.api.WorkoutLogResponse

class WorkoutRepository(context: Context? = null) {

    private val api = RetrofitClient.apiService

    private val context: Context? = null
    private val db = if (context != null) {
        Room.databaseBuilder(context.applicationContext, MovaDatabase::class.java, "mova_database")
            .fallbackToDestructiveMigration()
            .build()
    } else null

    private val workoutDao = db?.workoutDao()
    private val exerciseDao = db?.exerciseDao()
    private val workoutExerciseDao = db?.workoutExerciseDao()

    private fun isOnline(): Boolean {
        if (context == null) return true
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun getAllExercises(): List<Exercise> {
        return try {
            if (!isOnline()) throw Exception("Offline")
            val apiExercises = api.getAllExercises()
            exerciseDao?.insertExercises(apiExercises.map { it.toEntity() })
            apiExercises
        } catch (e: Exception) {
            exerciseDao?.getAllExercises()?.map { it.toDomain() } ?: emptyList()
        }
    }

    suspend fun getSystemWorkouts(): List<Workout> {
        return try {
            if (!isOnline()) throw Exception("Offline")
            val apiWorkouts = api.getWorkoutsByUser("system")
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            workoutDao?.getAllWorkouts()?.filter { it.userId == "system" }?.map { it.toDomain() } ?: emptyList()
        }
    }

    suspend fun getUserWorkouts(userId: String): List<Workout> {
        return try {
            if (!isOnline()) throw Exception("Offline")
            val apiWorkouts = api.getWorkoutsByUser(userId)
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            workoutDao?.getAllWorkouts()?.filter { it.userId == userId }?.map { it.toDomain() } ?: emptyList()
        }
    }

    suspend fun getCustomWorkouts(userId: String): List<Workout> {
        return try {
            if (!isOnline()) throw Exception("Offline")
            val apiWorkouts = api.getWorkoutsByUser(userId, isCustom = true)
            workoutDao?.insertWorkouts(apiWorkouts.map { it.toEntity() })
            apiWorkouts
        } catch (e: Exception) {
            workoutDao?.getAllWorkouts()?.filter { it.userId == userId && it.isCustom }?.map { it.toDomain() } ?: emptyList()
        }
    }

    suspend fun getWorkoutById(workoutId: String): Workout {
        return try {
            if (!isOnline()) throw Exception("Offline")
            val workout = api.getWorkoutById(workoutId)

            // CACHE WORKOUT
            workoutDao?.insertWorkouts(listOf(workout.toEntity()))

            // CACHE EXERCISES
            if (workout.exercises.isNotEmpty()) {
                workoutExerciseDao?.insertWorkoutExercises(workout.exercises.map { it.toEntity() })
            }

            workout
        } catch (e: Exception) {
            // OFFLINE FALLBACK
            val cached = workoutDao?.getWorkoutById(workoutId) ?: throw Exception("Workout not found offline")

            // Get the cached exercises for this workout
            val cachedExercises = workoutExerciseDao?.getExercisesForWorkout(workoutId)?.map { it.toDomain() } ?: emptyList()

            cached.toDomain().copy(exercises = cachedExercises)
        }
    }

    suspend fun logWorkout(request: WorkoutLogRequest): WorkoutLogResponse {
        if (!isOnline()) throw Exception("Offline")
        return api.logWorkout(request)
    }

    suspend fun createWorkout(workout: Workout): Workout {
        if (!isOnline()) throw Exception("Cannot create workout offline")
        return api.createWorkout(workout)
    }

    suspend fun createWorkoutExercises(workoutId: String, exercises: List<WorkoutExercise>): List<WorkoutExercise> {
        if (!isOnline()) throw Exception("Cannot create workout offline")
        return api.createWorkoutExercises(workoutId, exercises)
    }

    suspend fun getWorkoutLogs(userId: String): List<WorkoutLog> {
        return api.getWorkoutLogs(userId)
    }
}

// MAPPERS
fun Workout.toEntity() = WorkoutEntity(
    workoutId = workoutId, userId = userId, name = name, description = description,
    difficulty = difficulty, durationMinutes = durationMinutes, isCustom = isCustom, iconName = icon
)

fun WorkoutEntity.toDomain() = Workout(
    workoutId = workoutId, userId = userId, name = name, description = description,
    difficulty = difficulty, durationMinutes = durationMinutes, isCustom = isCustom, icon = iconName,
    exercises = emptyList() // Exercises are loaded separately
)

fun WorkoutExercise.toEntity() = WorkoutExerciseEntity(
    workoutExerciseId = workoutExerciseId ?: "",
    workoutId = workoutId, exerciseId = exerciseId, orderNumber = orderNumber,
    sets = sets, reps = reps, restSeconds = restSeconds
)

fun WorkoutExerciseEntity.toDomain() = WorkoutExercise(
    workoutExerciseId = workoutExerciseId, workoutId = workoutId, exerciseId = exerciseId,
    orderNumber = orderNumber, sets = sets, reps = reps, restSeconds = restSeconds
)

fun Exercise.toEntity() = ExerciseEntity(
    exerciseId = exerciseId, name = name, description = description,
    targetMuscleGroup = targetMuscleGroup, difficulty = difficulty,
    videoUrl = videoUrl ?: "", metValue = metValue, isActive = isActive
)

fun ExerciseEntity.toDomain() = Exercise(
    exerciseId = exerciseId, name = name, description = description,
    targetMuscleGroup = targetMuscleGroup, difficulty = difficulty,
    videoUrl = videoUrl, metValue = metValue, isActive = isActive
)