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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mova.data.models.User

@Composable
fun EditProfileScreen(
    onBackPressed: () -> Unit = {},
    onSave: (User) -> Unit = {} // Callback when user saves changes
) {
    // Log screen entry
    Log.d("EditProfileScreen", "Edit Profile screen loaded")

    // TODO: Replace with actual user data from ViewModel
    // Current user data (mock data - will be fetched from ViewModel)
    var fullName by remember { mutableStateOf("Katlego Mokwena") }
    var height by remember { mutableStateOf("178") }
    var weight by remember { mutableStateOf("76") }
    var age by remember { mutableStateOf("26") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedFitnessLevel by remember { mutableStateOf("Intermediate") }
    var fitnessGoal by remember { mutableStateOf("Burn 2000 cal weekly") }

    // Options for dropdowns
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val fitnessLevelOptions = listOf("Beginner", "Intermediate", "Advanced", "Athlete")

    // Validation state
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

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

            // ===== Top Bar with Back Button and Title =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = "Edit Profile",
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

            // ===== Profile Picture Section =====
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar with edit icon overlay
                    Box(
                        modifier = Modifier.size(100.dp)
                    ) {
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFFB45309)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                        // Edit icon overlay at bottom right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MovaTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✏️",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to change photo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MovaGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Personal Information Form =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Full Name
                    EditProfileTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            Log.d("EditProfileScreen", "Name updated: $it")
                        },
                        error = errors["fullName"],
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Height and Weight in a Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EditProfileTextField(
                            label = "Height (cm)",
                            value = height,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    height = it
                                    Log.d("EditProfileScreen", "Height updated: $it cm")
                                }
                            },
                            keyboardType = KeyboardType.Number,
                            error = errors["height"],
                            modifier = Modifier.weight(1f)
                        )
                        EditProfileTextField(
                            label = "Weight (kg)",
                            value = weight,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                                    weight = it
                                    Log.d("EditProfileScreen", "Weight updated: $it kg")
                                }
                            },
                            keyboardType = KeyboardType.Number,
                            error = errors["weight"],
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Age
                    EditProfileTextField(
                        label = "Age",
                        value = age,
                        onValueChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                age = it
                                Log.d("EditProfileScreen", "Age updated: $it")
                            }
                        },
                        keyboardType = KeyboardType.Number,
                        error = errors["age"],
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender Dropdown
                    EditProfileDropdown(
                        label = "Gender",
                        options = genderOptions,
                        selectedOption = selectedGender,
                        onOptionSelected = {
                            selectedGender = it
                            Log.d("EditProfileScreen", "Gender updated: $it")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fitness Level Dropdown
                    EditProfileDropdown(
                        label = "Fitness Level",
                        options = fitnessLevelOptions,
                        selectedOption = selectedFitnessLevel,
                        onOptionSelected = {
                            selectedFitnessLevel = it
                            Log.d("EditProfileScreen", "Fitness Level updated: $it")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fitness Goal
                    EditProfileTextField(
                        label = "Fitness Goal",
                        value = fitnessGoal,
                        onValueChange = {
                            fitnessGoal = it
                            Log.d("EditProfileScreen", "Fitness Goal updated: $it")
                        },
                        error = errors["fitnessGoal"],
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Save Button =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onBackPressed,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MovaBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        // Validate inputs before saving
                        val validationErrors = validateForm(fullName, height, weight, age)
                        errors = validationErrors

                        if (validationErrors.isEmpty()) {
                            // Create updated user object
                            val updatedUser = User(
                                fullName = fullName,
                                location = "Johannesburg, SA", // TODO: Add location field
                                height = height.toDoubleOrNull(),
                                weight = weight.toDoubleOrNull(),
                                age = age.toIntOrNull(),
                                fitnessLevel = selectedFitnessLevel
                            )
                            Log.d("EditProfileScreen", "Saving user: $updatedUser")
                            onSave(updatedUser)
                            onBackPressed()
                        } else {
                            Log.w("EditProfileScreen", "Validation errors: $validationErrors")
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MovaBlue
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Changes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * EditProfileTextField - Reusable text input field with label and error handling
 */
@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MovaBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            isError = error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MovaTeal,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                errorBorderColor = Color.Red,
                focusedLabelColor = MovaTeal
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red
            )
        }
    }
}

/**
 * EditProfileDropdown - Reusable dropdown selector
 */
@Composable
fun EditProfileDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MovaBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MovaBlack
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedOption.isNotEmpty()) MovaBlack else MovaGray
                )
                Text(
                    text = "▼",
                    color = MovaGray,
                    fontSize = 14.sp
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(MovaOffWhite)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                color = if (option == selectedOption) MovaTeal else MovaBlack
                            )
                            if (option == selectedOption) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MovaTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Validate form inputs before saving
 */
private fun validateForm(
    fullName: String,
    height: String,
    weight: String,
    age: String
): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (fullName.isBlank()) {
        errors["fullName"] = "Name is required"
    }

    if (height.isBlank()) {
        errors["height"] = "Height is required"
    } else if (height.toDoubleOrNull() == null || height.toDouble() < 50 || height.toDouble() > 300) {
        errors["height"] = "Enter valid height (50-300 cm)"
    }

    if (weight.isBlank()) {
        errors["weight"] = "Weight is required"
    } else if (weight.toDoubleOrNull() == null || weight.toDouble() < 10 || weight.toDouble() > 500) {
        errors["weight"] = "Enter valid weight (10-500 kg)"
    }

    if (age.isBlank()) {
        errors["age"] = "Age is required"
    } else if (age.toIntOrNull() == null || age.toInt() < 1 || age.toInt() > 150) {
        errors["age"] = "Enter valid age (1-150)"
    }

    return errors
}