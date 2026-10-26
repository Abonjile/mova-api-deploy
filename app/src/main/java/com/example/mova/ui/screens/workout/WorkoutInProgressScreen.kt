package com.example.mova.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.remote.api.WorkoutLogRequest
import com.example.mova.data.repository.WorkoutRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

// Phases of the workout screen
private enum class Phase { SETUP, EXERCISING, COMPLETE }

private data class WorkoutSummary(
    val totalCaloriesBurned: Int,
    val exercisesCompleted: Int,
    val totalDurationMinutes: Int
)

@Composable
fun WorkoutInProgressScreen(
    workoutId: String,
    userId: String = "test_user_123",
    onBack: () -> Unit = {},
    onFinish: () -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    // Data state
    var workout by remember { mutableStateOf<Workout?>(null) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Timer state
    var phase by remember { mutableStateOf(Phase.SETUP) }
    var secondsRemaining by remember { mutableStateOf(10) }
    var isPaused by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }

    // Analytics counters
    var exercisesCompleted by remember { mutableStateOf(0) }
    var totalSecondsElapsed by remember { mutableStateOf(0) }

    // Final result
    var finalResult by remember { mutableStateOf<WorkoutSummary?>(null) }

    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load workout + exercises
    LaunchedEffect(workoutId) {
        try {
            isLoading = true
            val w = repository.getWorkoutById(workoutId)
            val allEx = repository.getAllExercises()
            val map = allEx.associateBy { it.exerciseId }
            workout = w
            exercises = w.exercises.sortedBy { it.orderNumber }.mapNotNull { map[it.exerciseId] }
        } catch (_: Exception) {
            // Handled via empty exercises state
        } finally {
            isLoading = false
        }
    }

    // Pause the timer when the app goes to background or closes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                isPaused = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Start the setup timer when the screen loads a workout
    LaunchedEffect(workout, exercises) {
        if (workout != null && exercises.isNotEmpty() && phase == Phase.SETUP && !isRunning && finalResult == null) {
            isRunning = true
            secondsRemaining = 10
        }
    }

    // Timer loop
    LaunchedEffect(isRunning, isPaused, phase, currentIndex) {
        if (!isRunning || isPaused) return@LaunchedEffect

        while (isRunning && !isPaused && secondsRemaining > 0) {
            delay(1000L)
            if (isRunning && !isPaused) {
                secondsRemaining--
                totalSecondsElapsed++
            }
        }

        if (secondsRemaining <= 0 && isRunning && !isPaused) {
            if (phase == Phase.SETUP) {
                // Move from setup to exercising
                phase = Phase.EXERCISING
                val currentWorkoutExercise = workout?.exercises?.firstOrNull { it.exerciseId == exercises[currentIndex].exerciseId }
                secondsRemaining = getExerciseDuration(exercises[currentIndex], currentWorkoutExercise?.sets ?: 1)
            } else if (phase == Phase.EXERCISING) {
                // Exercise complete
                val updatedCompleted = exercisesCompleted + 1
                exercisesCompleted = updatedCompleted

                if (currentIndex < exercises.size - 1) {
                    // Advance to next exercise
                    currentIndex++
                    phase = Phase.SETUP
                    secondsRemaining = 10
                } else {
                    // Last exercise done → finish the workout
                    isRunning = false
                    phase = Phase.COMPLETE
                    finalResult = finishWorkoutSession(
                        repository = repository,
                        userId = userId,
                        workoutId = workoutId,
                        exercisesCompleted = updatedCompleted,
                        totalSecondsElapsed = totalSecondsElapsed
                    )
                }
            }
        }
    }

    // UI
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF00008B))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = workout?.name ?: "Workout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00008B)
            )
            Spacer(modifier = Modifier.weight(1f))
            if (phase != Phase.COMPLETE) {
                Text(
                    text = "${currentIndex + 1} / ${exercises.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00008B),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            exercises.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No exercises available", color = Color.Gray)
                }
            }

            phase == Phase.COMPLETE -> {
                if (finalResult == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val summary = finalResult!!
                    // Confetti completion screen
                    Box(Modifier.fillMaxSize()) {
                        KonfettiView(
                            modifier = Modifier.fillMaxSize(),
                            parties = listOf(
                                Party(
                                    emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(50),
                                    position = Position.Relative(0.5, 0.3),
                                    colors = listOf(0xFF00008B.toInt(), 0xFFE53935.toInt(), 0xFFFFC107.toInt())
                                )
                            )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🎉", fontSize = 64.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Workout Complete!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00008B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "${summary.totalCaloriesBurned} calories burned",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE53935)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${summary.exercisesCompleted} exercises completed · ${summary.totalDurationMinutes} min",
                                fontSize = 14.sp,
                                color = Color(0xFF757575)
                            )
                            Spacer(Modifier.height(48.dp))
                            Button(
                                onClick = onFinish,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00008B)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("BACK TO DASHBOARD", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            else -> {
                val current = exercises[currentIndex]
                val workoutExercise = workout?.exercises?.firstOrNull { it.exerciseId == current.exerciseId }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Exercise image
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = getExerciseImage(current.name)),
                                contentDescription = current.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                                    .padding(8.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = current.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00008B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "${workoutExercise?.sets ?: 3} sets × ${workoutExercise?.reps ?: 12} reps",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(Modifier.height(16.dp))

                        // Timer Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF4F6FA)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (phase == Phase.SETUP) "GET READY" else formatTime(secondsRemaining),
                                    fontSize = if (phase == Phase.SETUP) 22.sp else 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (phase == Phase.SETUP) Color(0xFF757575) else Color(0xFF00008B)
                                )

                                if (phase == Phase.SETUP) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Starting in ${secondsRemaining}s",
                                        fontSize = 13.sp,
                                        color = Color(0xFF757575)
                                    )
                                } else {
                                    Spacer(Modifier.height(10.dp))
                                    val total = getExerciseDuration(current, workoutExercise?.sets ?: 1).toFloat()
                                    val progressValue = if (total > 0) ((total - secondsRemaining) / total).coerceIn(0f, 1f) else 0f
                                    LinearProgressIndicator(
                                        progress = progressValue,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = Color(0xFF00008B),
                                        trackColor = Color(0xFFE0E0E0)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    // Controls at bottom
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Pause/Play
                            if (phase == Phase.EXERCISING) {
                                OutlinedButton(
                                    onClick = { isPaused = !isPaused },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                        contentDescription = if (isPaused) "Play" else "Pause"
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isPaused) "Play" else "Pause", fontSize = 14.sp)
                                }
                            }

                            // Skip
                            OutlinedButton(
                                onClick = {
                                    if (currentIndex < exercises.size - 1) {
                                        currentIndex++
                                        phase = Phase.SETUP
                                        secondsRemaining = 10
                                        isPaused = false
                                    } else {
                                        isRunning = false
                                        phase = Phase.COMPLETE
                                        scope.launch {
                                            finalResult = finishWorkoutSession(
                                                repository = repository,
                                                userId = userId,
                                                workoutId = workoutId,
                                                exercisesCompleted = exercisesCompleted,
                                                totalSecondsElapsed = totalSecondsElapsed
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Skip")
                                Spacer(Modifier.width(4.dp))
                                Text("Skip", fontSize = 14.sp)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Quit
                        Button(
                            onClick = {
                                isRunning = false
                                phase = Phase.COMPLETE
                                scope.launch {
                                    finalResult = finishWorkoutSession(
                                        repository = repository,
                                        userId = userId,
                                        workoutId = workoutId,
                                        exercisesCompleted = exercisesCompleted,
                                        totalSecondsElapsed = totalSecondsElapsed
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("QUIT WORKOUT", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Returns the total active seconds for an exercise based on its difficulty.
// Beginner = 45s per set, Intermediate = 30s, Advanced = 20s.
private fun getExerciseDuration(exercise: Exercise, sets: Int): Int {
    val workSeconds = when (exercise.difficulty.uppercase()) {
        "BEGINNER" -> 45
        "INTERMEDIATE" -> 30
        "ADVANCED" -> 20
        else -> 45
    }
    return sets * workSeconds
}

// Formats seconds as MM:SS
private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

private suspend fun finishWorkoutSession(
    repository: WorkoutRepository,
    userId: String,
    workoutId: String,
    exercisesCompleted: Int,
    totalSecondsElapsed: Int
): WorkoutSummary {
    val durationMinutes = maxOf(1, totalSecondsElapsed / 60)
    return try {
        val response = repository.logWorkout(
            WorkoutLogRequest(
                userId = userId,
                workoutId = workoutId,
                durationMinutes = durationMinutes,
                weightAtTime = 70.0,
                userNote = ""
            )
        )
        WorkoutSummary(
            totalCaloriesBurned = response.calculatedCalories,
            exercisesCompleted = exercisesCompleted,
            totalDurationMinutes = durationMinutes
        )
    } catch (_: Exception) {
        WorkoutSummary(
            totalCaloriesBurned = exercisesCompleted * 30,
            exercisesCompleted = exercisesCompleted,
            totalDurationMinutes = durationMinutes
        )
    }
}
