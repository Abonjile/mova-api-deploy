package com.example.mova.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mova.data.models.Workout
import com.example.mova.data.models.Exercise
import com.example.mova.data.repository.WorkoutRepository

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    onBack: () -> Unit = {},
    onStartWorkout: (String) -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    var exerciseMap by remember { mutableStateOf<Map<String, Exercise>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(workoutId) {
        try {
            isLoading = true
            val w = repository.getWorkoutById(workoutId)
            val allExercises = repository.getAllExercises()
            workout = w
            exerciseMap = allExercises.associateBy { it.exerciseId }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Could not load workout: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF00008B)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = workout?.name ?: "Workout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00008B)
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(errorMessage ?: "", color = Color.Red, fontSize = 14.sp)
                }
            }
            workout != null -> {
                val w = workout!!

                // Big workout image (~1/3 of screen)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getWorkoutImage(w.name)),
                            contentDescription = w.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Meta info
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = w.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00008B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = w.description,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${w.difficulty} · ${w.durationMinutes} min · ${w.exercises.size} exercises",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF00008B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Exercises list
                Text(
                    text = "Exercises",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00008B),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                w.exercises.sortedBy { it.orderNumber }.forEachIndexed { index, we ->
                    val ex = exerciseMap[we.exerciseId]
                    ExerciseRow(
                        index = index + 1,
                        exerciseName = ex?.name ?: "Exercise",
                        sets = we.sets,
                        reps = we.reps,
                        targetMuscle = ex?.targetMuscleGroup ?: ""
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // START button
                Button(
                    onClick = { onStartWorkout(w.workoutId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "START WORKOUT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    index: Int,
    exerciseName: String,
    sets: Int,
    reps: Int,
    targetMuscle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF00008B), shape = RoundedCornerShape(50))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$index",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                if (targetMuscle.isNotEmpty()) {
                    Text(
                        text = targetMuscle,
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
            Text(
                text = "$sets × $reps",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00008B)
            )
        }
    }
}