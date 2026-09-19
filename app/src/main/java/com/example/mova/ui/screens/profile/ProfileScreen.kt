package com.example.mova.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.mova.ui.theme.MovaBlue
import com.example.mova.ui.theme.MovaTeal
import com.example.mova.ui.theme.MovaOffWhite
import com.example.mova.ui.theme.MovaGray
import com.example.mova.ui.theme.MovaLightGray
import com.example.mova.ui.theme.MovaBlack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.mova.data.models.User

// PROFILE SCREEN - MAIN ENTRY POINT

@Composable
fun ProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {}
) {
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Log state changes for debugging
    LaunchedEffect(uiState) {
        Log.d("ProfileScreen", "UI State: isLoading=${uiState.isLoading}, user=${uiState.user?.fullName}")
    }

    Scaffold(
        containerColor = MovaOffWhite
    ) { innerPadding ->
        when {
            // ===== LOADING STATE =====
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MovaBlue)
                }
            }

            // ===== ERROR STATE =====
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌ ${uiState.error}",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadUser() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            // ===== CONTENT STATE (User data loaded) =====
            uiState.user != null -> {
                ProfileContent(
                    user = uiState.user!!,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToEdit = onNavigateToEdit
                )
            }

            // ===== EMPTY STATE =====
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No user data available")
                }
            }
        }
    }
}

// PROFILE CONTENT - DISPLAYS THE ACTUAL PROFILE UI

@Composable
fun ProfileContent(
    user: User,
    onNavigateToSettings: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    // Log the user data being displayed
    Log.d("ProfileContent", "Displaying user: ${user.fullName}, ${user.weight}kg, ${user.age} yrs")

    // Fitness objectives
    val objectives = listOf(
        "Burn 2000 cal weekly",
        "Complete 4 full body workouts",
        "Maintain 7-day active streak"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ===== Profile Header with Logo =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
            )
            // MOVA Logo with 2 dots (branding)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MovaBlack))
                Spacer(modifier = Modifier.width(2.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                Spacer(modifier = Modifier.width(4.dp))
                Text("MOVA", fontWeight = FontWeight.Bold, color = MovaBlue, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Profile Avatar =====
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Avatar with teal border ring
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MovaTeal)
                    .padding(4.dp)
            ) {
                // Placeholder avatar - Replace with actual image from API
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFB45309)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.take(2).uppercase(), // Initials
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== User Name and Location =====
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MovaBlue,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Text(
            text = user.location ?: "Location not set",
            style = MaterialTheme.typography.bodyMedium,
            color = MovaGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== Action Buttons (Edit & Settings) =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onNavigateToEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = MovaBlue)
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MovaBlack)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Stats Section (Weight, Height, Age & Fitness Level) =====
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Column - Weight, Height, Age
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "WEIGHT",
                    value = if (user.weight != null) "${user.weight} kg" else "Not set"
                )
                StatCard(
                    label = "HEIGHT",
                    value = if (user.height != null) "${user.height} cm" else "Not set"
                )
                StatCard(
                    label = "AGE",
                    value = if (user.age != null) "${user.age} yrs" else "Not set"
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Column - Fitness Level (Taller card)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "FITNESS LEVEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MovaGray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.fitnessLevel ?: "Not set",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MovaBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Fitness Objectives Card =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MovaBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Card Header with Add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fitness Objectives",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { /* TODO: Show dialog to add new objective */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of objectives
                objectives.forEach { objective ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Green checkmark dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MovaTeal)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = objective,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        // Delete button for each objective
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Objective",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// STAT CARD - REUSABLE COMPONENT

/**
 * StatCard - Displays a single stat with label and value
 * Used for Weight, Height, Age cards
 */
@Composable
fun StatCard(
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MovaLightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MovaGray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
            )
        }
    }
}