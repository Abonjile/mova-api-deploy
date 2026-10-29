/**
 * =====================================================================================
 * COMPOSABLE: ChallengesScreen
 * =====================================================================================
 * Shows the programmes the user has joined as tabs, each with its progress, and a list
 * of programmes they can still join. Every label comes from strings.xml.
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mova.R
import com.example.mova.data.remote.model.ChallengeDto
import com.example.mova.data.remote.model.ProgramDto

@Composable
fun ChallengesScreen(
    userId: String,
    viewModel: ChallengesViewModel = viewModel()
) {
    val joined by viewModel.joined.collectAsState()
    val catalogue by viewModel.catalogue.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Reload whenever the screen is shown. ON_RESUME also fires when the observer is added,
    // so this covers the first open and every return from another screen, such as a workout.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, userId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh(userId)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Which joined programme tab is open. Clamped so it never points past the list.
    var selectedTab by remember { mutableStateOf(0) }
    val tabIndex = selectedTab.coerceIn(0, (joined.size - 1).coerceAtLeast(0))

    val available = catalogue.filter { !it.joined }
    // A local copy lets the compiler smart-cast the value in the when branches below.
    val currentError = error

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // ---- Header ----
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.challenges_title),
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
                text = stringResource(R.string.challenges_active_programs),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ---- Joined programmes: loading, error, empty, or tabs ----
        when {
            isLoading -> item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFD9820B))
                }
            }

            currentError != null -> item {
                Text(
                    text = errorText(currentError),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            joined.isEmpty() -> item {
                Text(
                    text = stringResource(R.string.challenges_none_joined),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            else -> {
                // One tab per joined programme.
                item {
                    ScrollableTabRow(
                        selectedTabIndex = tabIndex,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent
                    ) {
                        joined.forEachIndexed { index, challenge ->
                            Tab(
                                selected = tabIndex == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = challenge.title,
                                        maxLines = 1,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // The open programme's progress card.
                item {
                    ActiveProgramCard(
                        challenge = joined[tabIndex],
                        onCheckIn = { viewModel.checkIn(userId, joined[tabIndex].id) }
                    )
                }
            }
        }

        // ---- Programmes the user can still join ----
        if (available.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.challenges_available),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A237E)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(available, key = { it.id }) { program ->
                AvailableProgramCard(
                    program = program,
                    onJoin = { viewModel.join(userId, program.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/** Card for the open programme: progress, percentage and check-in. */
@Composable
private fun ActiveProgramCard(
    challenge: ChallengeDto,
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
                Text(
                    text = challenge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1C1E)
                )
                Text(
                    text = stringResource(
                        R.string.challenges_progress_days,
                        challenge.daysCompleted,
                        challenge.durationDays
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFD9820B)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = challenge.description, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress =  challenge.progressPercentage / 100f ,
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
                Text(
                    text = stringResource(R.string.challenges_overall_progress),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${challenge.progressPercentage}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (challenge.completed) {
                Text(
                    text = stringResource(R.string.challenges_completed),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.align(Alignment.End)
                )
            } else {
                OutlinedButton(
                    onClick = onCheckIn,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.challenges_check_in), fontSize = 12.sp)
                }
            }
        }
    }
}

/** Card for a programme the user has not joined yet. */
@Composable
private fun AvailableProgramCard(
    program: ProgramDto,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1C1E)
                )
                Text(text = program.description, fontSize = 11.sp, color = Color.Gray)
                Text(
                    text = stringResource(R.string.challenges_duration_days, program.durationDays),
                    fontSize = 11.sp,
                    color = Color(0xFFD9820B)
                )
            }
            Button(onClick = onJoin) {
                Text(text = stringResource(R.string.challenges_join), fontSize = 12.sp)
            }
        }
    }
}

/** Turns an error from the ViewModel into the matching translated message. */
@Composable
private fun errorText(error: ChallengeError): String = when (error) {
    ChallengeError.NETWORK -> stringResource(R.string.network_error)
    ChallengeError.LOAD_FAILED -> stringResource(R.string.error_load)
    ChallengeError.ACTION_FAILED -> stringResource(R.string.error_action)
    ChallengeError.ALREADY_DONE -> stringResource(R.string.error_already_done)
}