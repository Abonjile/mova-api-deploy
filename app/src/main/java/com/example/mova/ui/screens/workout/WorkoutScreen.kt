package com.example.mova.ui.screens.workout

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val MovaBlue = Color(0xFF1A1A2E)

@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        WorkoutHeader()
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))

        // Jump Back In
        SectionTitle("Jump Back In")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jumpBackInItems) { item ->
                LargeWorkoutCard(title = item)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Challenges
        SectionTitle("Challenges")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challengeItems) { item ->
                SmallCard(title = item)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exercises
        SectionTitle("Exercises")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exerciseItems) { item ->
                SmallCard(title = item)
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
        // "Workout" on the LEFT — dark blue, large
        Text(
            text = "Workout",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MovaBlue
        )

        // "● ● MOVA"on the RIGHT
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Black dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Black, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Red dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFFE53935), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            // MOVA text
            Text(
                text = "MOVA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
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
private fun LargeWorkoutCard(title: String) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MovaBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ─────────────────────────────────────────────
// SMALL CARD (Challenges & Exercises)
// ─────────────────────────────────────────────
@Composable
private fun SmallCard(title: String) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8EAF6)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MovaBlue
            )
        }
    }
}

// ─────────────────────────────────────────────
// SAMPLE DATA (replace with API data later)
// ─────────────────────────────────────────────
private val jumpBackInItems = listOf(
    "Morning Routine",
    "Full Body Blast",
    "Quick Cardio",
    "Core Crusher"
)

private val challengeItems = listOf(
    "7-Day Kickstart",
    "10-Day Burn",
    "30-Day Strong",
    "Beginner Boost",
    "Advanced Push"
)

private val exerciseItems = listOf(
    "Push-ups",
    "Squats",
    "Plank",
    "Lunges",
    "Burpees",
    "Jumping Jacks"
)