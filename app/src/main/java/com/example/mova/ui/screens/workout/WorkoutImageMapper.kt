package com.example.mova.ui.screens.workout

import com.example.mova.R

/**
 * Maps a workout name from the backend to the local drawable image.
 * Fallback = a default image if no match is found.
 */
fun getWorkoutImage(workoutName: String): Int {
    return when (workoutName.lowercase().trim()) {
        "abs workout"          -> R.drawable.abs_workout
        "hiit workout"         -> R.drawable.hiit_workout
        "arm strength"         -> R.drawable.armstrength_workout
        "back workout"         -> R.drawable.back_workout
        "back builder"         -> R.drawable.backbuilder_workout
        "cardio blast"         -> R.drawable.cardio_workout
        "chest workout"        -> R.drawable.chest_workout
        "core crush"           -> R.drawable.corecrush_workout
        "full body"            -> R.drawable.fullbody_workout
        "glute power"          -> R.drawable.glutepower_workout
        "leg day"              -> R.drawable.legday_workout
        "low impact"           -> R.drawable.lowimpact_workout
        // Extras you have images for
        "yoga"                 -> R.drawable.yoga_workout
        "mobility"             -> R.drawable.mobility_workout
        "glute workout"        -> R.drawable.glute_workout
        else                   -> R.drawable.fullbody_workout // fallback
    }
}