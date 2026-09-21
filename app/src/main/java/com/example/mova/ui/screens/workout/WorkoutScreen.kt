package com.example.mova.ui.screens.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    repository: WorkoutRepository = WorkoutRepository()
) {
    var systemWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var userWorkouts by remember { mutableStateOf<List<Workout>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch workouts on first composition
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            systemWorkouts = repository.getSystemWorkouts()
            // TODO: Replace with real logged-in user ID later
            userWorkouts = repository.getUserWorkouts("test_user_123")
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Could not load workouts: ${e.message}"
        } finally {
            isLoading = false
        }
    }

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
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
            else -> {
                // Jump Back In — user's recent (for now, just first few)
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
                                imageRes = getWorkoutImage(workout.name)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Challenges — first 5 system workouts (a subset for now)
                if (systemWorkouts.isNotEmpty()) {
                    SectionTitle("Challenges")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(systemWorkouts.take(5)) { workout ->
                            SmallCard(
                                workout = workout,
                                imageRes = getWorkoutImage(workout.name)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Exercises — rest of the system workouts
                if (systemWorkouts.size > 5) {
                    SectionTitle("Exercises")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(systemWorkouts.drop(5)) { workout ->
                            SmallCard(
                                workout = workout,
                                imageRes = getWorkoutImage(workout.name)
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

// ─────────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// SEARCH BAR
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// SECTION TITLE
// ─────────────────────────────────────────────
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

// ─────────────────────────────────────────────
// LARGE CARD (Jump Back In)
// ─────────────────────────────────────────────
@Composable
private fun LargeWorkoutCard(workout: Workout, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp),
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

// ─────────────────────────────────────────────
// SMALL CARD (Challenges & Exercises)
// ─────────────────────────────────────────────
@Composable
private fun SmallCard(workout: Workout, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(130.dp),
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