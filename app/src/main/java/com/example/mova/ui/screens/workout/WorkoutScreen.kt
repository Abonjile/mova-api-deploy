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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mova.data.models.Workout
import com.example.mova.data.repository.WorkoutRepository

/**
 * WorkoutScreen is the main dashboard for the workout feature.
 *
 * Responsibilities:
 *  - Fetches system workouts and user-specific workouts from the backend API.
 *  - Displays three horizontal scrolling sections:
 *      1. "Jump Back In"  – recent / ongoing workouts
 *      2. "Challenges"    – a curated subset of workouts
 *      3. "Exercises"     – the remaining system workouts
 *  - Handles loading and error states gracefully.
 *
 * @param modifier       Modifier from the parent composable.
 * @param onWorkoutClick Callback invoked when a card is tapped, passing the workoutId.
 * @param repository     Data repository used to fetch workouts (injected for testability).
 */
@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    onWorkoutClick: (String) -> Unit = {},
    repository: WorkoutRepository = WorkoutRepository()
) {
    // ── State ─────────────────────────────────────────────────────────────
    var systemWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var userWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ── Load data on first composition ────────────────────────────────────
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            systemWorkouts = repository.getSystemWorkouts()
            // TODO: Replace with real logged-in user ID once authentication is wired up.
            userWorkouts = repository.getUserWorkouts("test_user_123")
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Could not load workouts: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        WorkoutHeader()
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))

        when {
            // Loading spinner while API request is in flight
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error state if the API call fails
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }

            // Data loaded — render sections
            else -> {
                // ── Jump Back In ─────────────────────────────────────────
                // Currently falls back to system workouts if the user has no history.
                // Will be replaced with real "recently viewed" tracking in a later phase.
                val recent = userWorkouts.ifEmpty { systemWorkouts }.take(5)
                if (recent.isNotEmpty()) {
                    SectionTitle("Jump Back In")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recent) { workout ->
                            LargeWorkoutCard(
                                workout = workout,
                                imageRes = getWorkoutImage(workout.name),
                                onClick = { onWorkoutClick(workout.workoutId) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Challenges ───────────────────────────────────────────
                // Currently the first 5 system workouts. Will be tied to actual
                // challenge data in a later phase.
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
                                onClick = { onWorkoutClick(workout.workoutId) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ── Exercises ────────────────────────────────────────────
                // Remaining system workouts after the first 5.
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
                                onClick = { onWorkoutClick(workout.workoutId) }
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

// ─────────────────────────────────────────────────────────────────────────
// HEADER — "Workout" on the left, "MOVA ● ●" on the right
// ─────────────────────────────────────────────────────────────────────────
@Composable
private fun WorkoutHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Workout",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00008B)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Dark blue dot (matches primary brand colour)
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF00008B), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Red accent dot
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

// ─────────────────────────────────────────────────────────────────────────
// SEARCH BAR — non-functional placeholder for the POE phase
// ─────────────────────────────────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────
// SECTION TITLE — small gray header above each horizontal row
// ─────────────────────────────────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────
// LARGE CARD — used in the "Jump Back In" section
// Tap → navigates to the workout detail screen.
// ─────────────────────────────────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────
// SMALL CARD — used in the "Challenges" and "Exercises" sections
// Tap → navigates to the workout detail screen.
// ─────────────────────────────────────────────────────────────────────────
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