package com.example.mova.data.local

import android.content.Context
import android.content.SharedPreferences

// Stores the IDs of the last 5 workouts the user has tapped.
// Used to power the "Jump Back In" section on the dashboard.
class RecentWorkoutsStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("mova_recent_workouts", Context.MODE_PRIVATE)

    // Adds a workout ID to the front of the recent list.
    // Removes duplicates and keeps only the 5 most recent.
    fun addRecent(workoutId: String) {
        val current = getRecentIds().toMutableList()
        current.remove(workoutId)
        current.add(0, workoutId)
        val trimmed = current.take(5)
        prefs.edit().putString(KEY, trimmed.joinToString(",")).apply()
    }

    // Returns the list of recently viewed workout IDs.
    fun getRecentIds(): List<String> {
        val raw = prefs.getString(KEY, "") ?: ""
        return if (raw.isEmpty()) emptyList() else raw.split(",")
    }

    companion object {
        private const val KEY = "recent_ids"
    }
}