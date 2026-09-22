package com.example.mova.ui.screens.workout

import com.example.mova.R

// Maps an exercise name from the backend to its local tutorial image.
// Fallback is push-up tutorial if no match found.
fun getExerciseImage(exerciseName: String): Int {
    return when (exerciseName.lowercase().trim()) {
        "push-ups", "push ups", "pushup" -> R.drawable.pushup_tutorial
        "squats", "squat" -> R.drawable.squats_tutorial
        "plank" -> R.drawable.plank_tutorial
        "lunges", "lunge" -> R.drawable.lunges_tutorial
        "burpees", "burpee" -> R.drawable.burpee_tutorial
        "jumping jacks", "jumping jacks" -> R.drawable.jumpingjacks_tutorial
        "mountain climbers", "mountain climber" -> R.drawable.mountainclimbers_tutorial
        "glute bridges", "glute bridge" -> R.drawable.glutebridges_tutorial
        "donkey kicks", "donkey kick" -> R.drawable.donkeykick_tutorial
        "bicycle crunches", "bicycle crunch" -> R.drawable.bicyclecrunches_tutorial
        "russian twists", "russian twist" -> R.drawable.russiantwsits_tutorial
        "tricep dips", "tricep dip" -> R.drawable.tricepdips_tutorial
        "bicep curls", "bicep curl" -> R.drawable.bicepcurls_tutorial
        "shoulder taps", "shoulder tap" -> R.drawable.shouldertaps_tutorial
        "high knees", "high knee" -> R.drawable.highknees_tutorial
        "wall sit", "wall sits" -> R.drawable.wallsit_tutorial
        else -> R.drawable.pushup_tutorial // fallback
    }
}