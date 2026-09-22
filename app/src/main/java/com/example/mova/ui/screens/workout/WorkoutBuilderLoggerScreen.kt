@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.mova.ui.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mova.data.models.Exercise
import com.example.mova.data.models.Workout
import com.example.mova.data.models.WorkoutExercise
import com.example.mova.data.remote.api.WorkoutLogRequest
import com.example.mova.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

// One exercise selected in the builder
data class SelectedExercise(
    val exercise: Exercise,
    var sets: Int = 3,
    var reps: Int = 12
)

// Icon choices for a custom workout
data class IconOption(val name: String, val icon: ImageVector)

val iconOptions = listOf(
    IconOption("fitness", Icons.Default.FitnessCenter),
    IconOption("fire", Icons.Default.LocalFireDepartment),
    IconOption("star", Icons.Default.Star),
    IconOption("run", Icons.Default.DirectionsRun)
)

// Hardcoded until profile is wired up
private const val DEFAULT_USER_WEIGHT_KG = 70.0
private const val SECONDS_PER_REP = 3.0

@Composable
fun WorkoutBuilderLoggerScreen(
    userId: String = "test_user_123",
    onBack: () -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form state
    var workoutName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("BEGINNER") }
    var selectedIcon by remember { mutableStateOf("fitness") }
    var allExercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var selectedExercises by remember { mutableStateOf<List<SelectedExercise>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Load exercises from backend
    LaunchedEffect(Unit) {
        try {
            allExercises = repository.getAllExercises()
        } catch (e: Exception) {
            // silent
        } finally {
            isLoading = false
        }
    }

    // Live calorie calc
    val totalCalories = remember(selectedExercises) {
        selectedExercises.sumOf { sel ->
            val mins = (sel.reps * SECONDS_PER_REP * sel.sets) / 60.0
            val hours = mins / 60.0
            sel.exercise.metValue * DEFAULT_USER_WEIGHT_KG * hours
        }.toInt()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
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
                    "Create or Log",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00008B)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Workout name
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty
                Text("Difficulty", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF00008B))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("BEGINNER", "INTERMEDIATE", "ADVANCED").forEach { level ->
                        FilterChip(
                            selected = difficulty == level,
                            onClick = { difficulty = level },
                            label = { Text(level, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon picker
                Text("Workout Icon", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF00008B))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    iconOptions.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (selectedIcon == option.name) Color(0xFF00008B) else Color(0xFFF0F0F0),
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = option.name },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.name,
                                tint = if (selectedIcon == option.name) Color.White else Color(0xFF00008B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Exercises
                Text("Select Exercises", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF00008B))
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    allExercises.forEach { exercise ->
                        val selected = selectedExercises.firstOrNull { it.exercise.exerciseId == exercise.exerciseId }
                        ExerciseCard(
                            exercise = exercise,
                            selectedExercise = selected,
                            onToggle = { checked ->
                                selectedExercises = if (checked) {
                                    selectedExercises + SelectedExercise(exercise)
                                } else {
                                    selectedExercises.filterNot { it.exercise.exerciseId == exercise.exerciseId }
                                }
                            },
                            onSetsChange = { newSets ->
                                selectedExercises = selectedExercises.map {
                                    if (it.exercise.exerciseId == exercise.exerciseId) it.copy(sets = newSets) else it
                                }
                            },
                            onRepsChange = { newReps ->
                                selectedExercises = selectedExercises.map {
                                    if (it.exercise.exerciseId == exercise.exerciseId) it.copy(reps = newReps) else it
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description / Note field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Note") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Big calorie card
                Card(
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 40.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Estimated Energy Burnt", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            Text(
                                "$totalCalories kcal",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Yellow Log button
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                isSaving = true
                                // For logging, we need a workoutId. If user built their own,
                                // we first create the workout then log it.
                                val workout = Workout(
                                    userId = userId,
                                    name = workoutName,
                                    description = description,
                                    difficulty = difficulty,
                                    durationMinutes = (selectedExercises.sumOf { it.sets * it.reps } * 3) / 60,
                                    isCustom = true
                                )
                                val created = repository.createWorkout(workout)
                                val weList = selectedExercises.mapIndexed { index, sel ->
                                    WorkoutExercise(
                                        workoutId = created.workoutId,
                                        exerciseId = sel.exercise.exerciseId,
                                        orderNumber = index + 1,
                                        sets = sel.sets,
                                        reps = sel.reps
                                    )
                                }
                                if (weList.isNotEmpty()) {
                                    repository.createWorkoutExercises(created.workoutId, weList)
                                }
                                // Then log it
                                val logReq = WorkoutLogRequest(
                                    userId = userId,
                                    workoutId = created.workoutId,
                                    durationMinutes = created.durationMinutes,
                                    weightAtTime = DEFAULT_USER_WEIGHT_KG,
                                    userNote = description
                                )
                                repository.logWorkout(logReq)

                                snackbarHostState.showSnackbar("Workout logged!")
                                workoutName = ""
                                description = ""
                                selectedExercises = emptyList()
                                difficulty = "BEGINNER"
                                selectedIcon = "fitness"
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Could not log: ${e.message}")
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty() && !isSaving,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5C518)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (isSaving) "Saving..." else "LOG WORKOUT",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Orange Create button
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                isSaving = true
                                val workout = Workout(
                                    userId = userId,
                                    name = workoutName,
                                    description = description,
                                    difficulty = difficulty,
                                    durationMinutes = (selectedExercises.sumOf { it.sets * it.reps } * 3) / 60,
                                    isCustom = true
                                )
                                val created = repository.createWorkout(workout)
                                val weList = selectedExercises.mapIndexed { index, sel ->
                                    WorkoutExercise(
                                        workoutId = created.workoutId,
                                        exerciseId = sel.exercise.exerciseId,
                                        orderNumber = index + 1,
                                        sets = sel.sets,
                                        reps = sel.reps
                                    )
                                }
                                if (weList.isNotEmpty()) {
                                    repository.createWorkoutExercises(created.workoutId, weList)
                                }
                                snackbarHostState.showSnackbar("Workout created!")
                                workoutName = ""
                                description = ""
                                selectedExercises = emptyList()
                                difficulty = "BEGINNER"
                                selectedIcon = "fitness"
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Could not create: ${e.message}")
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = workoutName.isNotBlank() && selectedExercises.isNotEmpty() && !isSaving,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (isSaving) "Saving..." else "CREATE CUSTOM WORKOUT",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    selectedExercise: SelectedExercise?,
    onToggle: (Boolean) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(selectedExercise == null) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedExercise != null) Color(0xFFF5F5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = selectedExercise != null,
                    onCheckedChange = { onToggle(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text(exercise.targetMuscleGroup, fontSize = 12.sp, color = Color(0xFF757575))
                }
            }
            if (selectedExercise != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(start = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Stepper(
                        label = "Sets",
                        value = selectedExercise.sets,
                        onDecrement = { if (selectedExercise.sets > 1) onSetsChange(selectedExercise.sets - 1) },
                        onIncrement = { if (selectedExercise.sets < 10) onSetsChange(selectedExercise.sets + 1) }
                    )
                    Stepper(
                        label = "Reps",
                        value = selectedExercise.reps,
                        onDecrement = { if (selectedExercise.reps > 1) onRepsChange(selectedExercise.reps - 1) },
                        onIncrement = { if (selectedExercise.reps < 50) onRepsChange(selectedExercise.reps + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Stepper(
    label: String,
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Column {
        Text(label, fontSize = 11.sp, color = Color(0xFF757575))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }
            Text("$value", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
            }
        }
    }
}