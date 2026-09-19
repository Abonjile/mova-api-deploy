package com.example.mova.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

// Colors are imported from ProfileScreen (MovaBlue, MovaTeal, etc.), will be moved to a shared file later

@Composable
fun SettingsScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {} // Navigation callback to return to Profile
) {
    // Get current user from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = uiState.user

    // Log screen entry for debugging
    Log.d("SettingsScreen", "Settings screen loaded with user: ${currentUser?.fullName}")

    // State variables for settings toggles
    var darkMode by remember { mutableStateOf(false) }
    var largeText by remember { mutableStateOf(true) }
    var highContrast by remember { mutableStateOf(false) }
    var biometricAuth by remember(currentUser) { mutableStateOf(currentUser?.biometricEnabled ?: false) }
    var notificationsEnabled by remember(currentUser) { mutableStateOf(currentUser?.notificationsEnabled ?: true) }
    var selectedLanguage by remember(currentUser) { mutableStateOf(currentUser?.language ?: "English") }

    // Supported South African languages (from design doc)
    val languages = listOf(
        "English",
        "isiZulu",
        "Sesotho"
    )

    Log.d("SettingsScreen", "Current language: $selectedLanguage")

    // Main screen layout
    Scaffold(
        containerColor = MovaOffWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ===== Top Bar with Back Button and Logo =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back to Profile",
                            tint = MovaBlue
                        )
                    }
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MovaBlue
                    )
                }

                // MOVA Logo with dots (branding)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MovaBlack))
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("MOVA", fontWeight = FontWeight.Bold, color = MovaBlue, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Preferences Section =====
            SectionHeader(title = "Preferences")
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Dark Mode",
                        subtitle = "Optimise display for dark environments",
                        checked = darkMode,
                        onCheckedChange = {
                            darkMode = it
                            Log.d("SettingsScreen", "Dark Mode toggled: $it")
                            // TODO: Apply dark mode theme change
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    SettingToggleRow(
                        title = "Larger Text",
                        subtitle = "Increase readability across pages",
                        checked = largeText,
                        onCheckedChange = {
                            largeText = it
                            Log.d("SettingsScreen", "Larger Text toggled: $it")
                            // TODO: Apply text size change
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    SettingToggleRow(
                        title = "High Contrast",
                        subtitle = "Enhance visually contrasting boundaries",
                        checked = highContrast,
                        onCheckedChange = {
                            highContrast = it
                            Log.d("SettingsScreen", "High Contrast toggled: $it")
                            // TODO: Apply high contrast mode
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Security Section =====
            SectionHeader(title = "Security")
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        title = "Biometric Authentication",
                        subtitle = "Use fingerprint or face ID to log in",
                        checked = biometricAuth,
                        onCheckedChange = {
                            biometricAuth = it
                            Log.d("SettingsScreen", "Biometric toggled: $it")
                            // TODO: Enable/disable BiometricPrompt
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== App Language Section =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionHeader(title = "App Language")
                Text(
                    text = "South African Languages",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD946EF), // Pinkish Purple
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = language,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                                color = if (language == selectedLanguage) MovaTeal else MovaBlack
                            )
                            RadioButton(
                                selected = language == selectedLanguage,
                                onClick = {
                                    selectedLanguage = language
                                    Log.d("SettingsScreen", "Language changed to: $language")
                                    // TODO: Apply language change
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MovaTeal
                                )
                            )
                        }
                        // Add divider except for last item
                        if (language != languages.last()) {
                            Divider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = Color.Gray.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Save Button (optional - or settings auto-save) =====
            Button(
                onClick = {
                    Log.d("SettingsScreen", "Saving settings: lang=$selectedLanguage, notif=$notificationsEnabled, bio=$biometricAuth")
                    viewModel.updateSettings(
                        language = selectedLanguage,
                        notificationsEnabled = notificationsEnabled,
                        biometricEnabled = biometricAuth
                    )
                    onBackPressed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MovaBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Save Settings",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * SectionHeader - Reusable section title component
 * Used for Preferences, Security, App Language sections
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MovaBlue
    )
}

/**
 * SettingToggleRow - Reusable toggle row with title and subtitle
 * Used for Dark Mode, Biometric, etc.
 */
@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MovaGray
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Right side: Switch toggle
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MovaTeal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            )
        )
    }
}