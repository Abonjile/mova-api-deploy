package com.example.mova.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mova.data.local.RecentWorkoutsStore
import com.example.mova.data.models.Workout
import com.example.mova.data.repository.WorkoutRepository

// WorkoutScreen is the main dashboard for the workout feature.
// Loads system workouts, user-created workouts, and recent workouts.
// Sections: Jump Back In, Challenges, Exercises, My Workouts.
@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    onWorkoutClick: (String) -> Unit = {},
    onAddClick: () -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    // State
    var systemWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var userWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var customWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var recentWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val recentStore = remember { RecentWorkoutsStore(context) }

    // Load everything on first composition
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            systemWorkouts = repository.getSystemWorkouts()
            userWorkouts = repository.getUserWorkouts("test_user_123")
            customWorkouts = repository.getCustomWorkouts("test_user_123")

            // Resolve recent IDs into actual workout objects
            val recentIds = recentStore.getRecentIds()
            val allAvailable = systemWorkouts + customWorkouts
            recentWorkouts = recentIds.mapNotNull { id ->
                allAvailable.firstOrNull { it.workoutId == id }
            }

            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Could not load workouts: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Handles tapping a card: saves to recents, then navigates
    val onCardClick: (Workout) -> Unit = { workout ->
        recentStore.addRecent(workout.workoutId)
        onWorkoutClick(workout.workoutId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        WorkoutHeader(onAddClick = onAddClick)
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage ?: "", color = Color.Red, fontSize = 14.sp)
                }
            }
            else -> {
                // ── Jump Back In ──
                if (recentWorkouts.isNotEmpty()) {
                    SectionTitle("Jump Back In")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentWorkouts) { workout ->
                            if (workout.isCustom) {
                                LargeCustomWorkoutCard(
                                    workout = workout,
                                    onClick = { onCardClick(workout) }
                                )
                            } else {
                                LargeWorkoutCard(
                                    workout = workout,
                                    imageRes = getWorkoutImage(workout.name),
                                    onClick = { onCardClick(workout) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Challenges ──
                if (systemWorkouts.isNotEmpty()) {
                    SectionTitle("Challenges")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(systemWorkouts.take(5)) { workout ->
                            SmallCard(
                                workout = workout,
                                imageRes = getWorkoutImage(workout.name),
                                onClick = { onCardClick(workout) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Exercises ──
                if (systemWorkouts.size > 5) {
                    SectionTitle("Exercises")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(systemWorkouts.drop(5)) { workout ->
                            SmallCard(
                                workout = workout,
                                imageRes = getWorkoutImage(workout.name),
                                onClick = { onCardClick(workout) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── My Workouts ──
                if (customWorkouts.isNotEmpty()) {
                    SectionTitle("My Workouts")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(customWorkouts) { workout ->
                            CustomWorkoutCard(
                                workout = workout,
                                onClick = { onCardClick(workout) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// HEADER
@Composable
private fun WorkoutHeader(onAddClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Workout",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00008B)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Workout",
                    tint = Color(0xFF00008B)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF00008B), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFFE53935), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "MOVA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// SEARCH BAR
@Composable
private fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0F0F0))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Search exercises and workouts",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(20.dp)
        )
    }
}

// SECTION TITLE
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF757575),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp)
    )
}

// LARGE CARD — photo card for system workouts in Jump Back In
@Composable
private fun LargeWorkoutCard(
    workout: Workout,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = workout.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// LARGE CUSTOM CARD — no image, colored card for custom workouts in Jump Back In
@Composable
private fun LargeCustomWorkoutCard(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF00008B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon circle — picks the icon based on workout.icon
            val iconVector = when (workout.icon) {
                "fire" -> Icons.Default.LocalFireDepartment
                "star" -> Icons.Default.Star
                "run" -> Icons.Default.DirectionsRun
                else -> Icons.Default.FitnessCenter
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = workout.icon,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Workout info
            Column {
                Text(
                    text = workout.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                Text(
                    text = "${workout.difficulty} · ${workout.durationMinutes} min",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// SMALL CARD — used in Challenges and Exercises sections
@Composable
private fun SmallCard(
    workout: Workout,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = workout.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// CUSTOM CARD — small icon-based card for My Workouts
@Composable
private fun CustomWorkoutCard(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val iconVector = when (workout.icon) {
                "fire" -> Icons.Default.LocalFireDepartment
                "star" -> Icons.Default.Star
                "run" -> Icons.Default.DirectionsRun
                else -> Icons.Default.FitnessCenter
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF00008B), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = workout.icon,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = workout.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00008B),
                    maxLines = 2
                )
                Text(
                    text = workout.difficulty,
                    fontSize = 10.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}