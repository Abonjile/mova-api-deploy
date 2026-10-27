/**
 * =====================================================================================
 * COMPOSABLE: ChallengesScreen
 * =====================================================================================
 * Shows the user's active programmes with progress, and lets them record a check-in.
 * The target muscle group card is still hardcoded; it should come from data later.
 * =====================================================================================
 */
package com.example.mova.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mova.data.remote.model.ChallengeDto

@Composable
fun ChallengesScreen(
    userId: String,
    viewModel: ChallengesViewModel = viewModel()
) {
    // Each collectAsState turns a StateFlow into Compose state. When the value changes,
    // the UI rebuilds with the new value.
    val challenges by viewModel.challenges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Runs once per userId. Without this, the list stays empty when the screen first opens.
    LaunchedEffect(userId) {
        viewModel.fetchUserChallenges(userId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // ---- Header: title, logo and the muscle group card ----
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Challenges",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Black, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "MOVA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Target Muscle Groups",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Static placeholder card matching the prototype. Replace with real data later.
            Card(
                modifier = Modifier.width(140.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "C", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD9820B))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Legs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1C1E))
                    Text(text = "10 Workouts", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Active Programs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ---- Body: exactly one of these states is shown at a time ----
        // `when` picks the first branch that matches, so the order is the priority:
        // loading, then error, then empty, then the list.
        when {
            isLoading -> item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFD9820B))
                }
            }

            errorMessage != null -> item {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            challenges.isEmpty() -> item {
                Text(
                    text = "You haven't joined any challenges yet.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            else -> items(challenges, key = { it.id }) { challenge ->
                ActiveProgramCard(
                    title = challenge.title,
                    subtitle = challenge.description,
                    progressText = "${challenge.daysCompleted}/${challenge.durationDays} days",
                    progressValue = challenge.progressPercentage / 100f, // the bar needs 0.0 to 1.0
                    percentageString = "${challenge.progressPercentage}%",
                    completed = challenge.completed,
                    onCheckIn = { viewModel.checkIn(userId, challenge.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Card for one active programme.
 *
 * @param completed  Hides the check-in button once the programme is finished.
 * @param onCheckIn  Called when the user records today's workout.
 */
@Composable
fun ActiveProgramCard(
    title: String,
    subtitle: String,
    progressText: String,
    progressValue: Float,
    percentageString: String,
    completed: Boolean,
    onCheckIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1C1E))
                Text(progressText, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD9820B))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            // The lambda form lets the bar read its value during drawing rather than during
            // recomposition. If your Material3 version doesn't accept a lambda, use
            // `progress = progressValue` instead.
            LinearProgressIndicator(
                progress =  progressValue ,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFFD9820B),
                trackColor = Color(0xFFF1F3F5)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Overall progress", fontSize = 11.sp, color = Color.Gray)
                Text(percentageString, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            }

            // Only shown while the program is in progress. The server also limits check-in
            // to once per day, so tapping again shows a message instead of counting twice.
            if (!completed) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCheckIn,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Check in today", fontSize = 12.sp)
                }
            }
        }
    }
}