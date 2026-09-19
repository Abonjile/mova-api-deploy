package com.example.mova.data.models

/*
 * Request body for PUT /api/users/{userId}/settings
 * Strongly-typed to satisfy Retrofit's Gson converter.
 */
data class SettingsRequest(
    val language: String,
    val notificationsEnabled: Boolean,
    val biometricEnabled: Boolean
)
