package com.example.mova.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.repository.WorkoutRepository

@Composable
fun WorkoutInProgressScreen(
    workoutId: String,
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(workoutId) {
        try {
            isLoading = true
            val w = repository.getWorkoutById(workoutId)
            val allEx = repository.getAllExercises()
            val map = allEx.associateBy { it.exerciseId }
            workout = w
            exercises = w.exercises.sortedBy { it.orderNumber }.mapNotNull { map[it.exerciseId] }
        } catch (e: Exception) {
            // Silent fail for now
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${currentIndex + 1} / ${exercises.size}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00008B),
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            exercises.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exercises available", color = Color.Gray)
                }
            }
            else -> {
                val current = exercises[currentIndex]
                val workoutExercise = workout?.exercises?.firstOrNull { it.exerciseId == current.exerciseId }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Exercise image placeholder
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF0F0F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            // TODO: Replace with exercise animation/image
                            // For now, show the exercise name
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "💪",
                                    fontSize = 64.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = current.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00008B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = current.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00008B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${workoutExercise?.sets ?: 3} sets × ${workoutExercise?.reps ?: 12} reps",
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = current.targetMuscleGroup,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Next / Finish button
                    Button(
                        onClick = {
                            if (currentIndex < exercises.size - 1) {
                                currentIndex++
                            } else {
                                onFinish()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (currentIndex < exercises.size - 1) "NEXT EXERCISE" else "FINISH WORKOUT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}