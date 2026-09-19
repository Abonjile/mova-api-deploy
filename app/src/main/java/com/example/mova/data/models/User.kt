package com.example.mova.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * === User Data Model ===
 *
 * Will be used for both API responses and local Room database
 * */
data class User (
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val height: Double? = null,
    val weight: Double? = null,
    val age: Int? = null,
    val gender: String? = null,
    val fitnessLevel: String? = null,
    val fitnessGoal: String? = null,
    val objectives: List<String> = emptyList(),
    val location: String? = null,
    val language: String = "English",
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val speechEnabled: Boolean = false
)

/**
 * === Room Entity for local storage ===
 *
 * For Offline-first architecture
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val fullName: String,
    val email: String,
    val height: Double?,
    val weight: Double?,
    val age: Int?,
    val gender: String?,
    val fitnessLevel: String?,
    val fitnessGoal: String?,
    val objectives: List<String> = emptyList(),
    val location: String?,
    val language: String,
    val notificationsEnabled: Boolean,
    val biometricEnabled: Boolean,
    val speechEnabled: Boolean,
    val isSynced: Boolean = false // For offline sync tracking
)

/**
 * Extension function to convert Entity to Domain Model
 */
fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        fullName = fullName,
        email = email,
        height = height,
        weight = weight,
        age = age,
        gender = gender,
        fitnessLevel = fitnessLevel,
        fitnessGoal = fitnessGoal,
        objectives = objectives,
        location = location,
        language = language,
        notificationsEnabled = notificationsEnabled,
        biometricEnabled = biometricEnabled,
        speechEnabled = speechEnabled
    )
}

/**
 * Extension function to convert Domain Model to Entity
 */
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        fullName = fullName,
        email = email,
        height = height,
        weight = weight,
        age = age,
        gender = gender,
        fitnessLevel = fitnessLevel,
        fitnessGoal = fitnessGoal,
        objectives = objectives,
        location = location,
        language = language,
        notificationsEnabled = notificationsEnabled,
        biometricEnabled = biometricEnabled,
        speechEnabled = speechEnabled,
        isSynced = false
    )
}