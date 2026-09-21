package com.example.mova.ui.screens.onboarding

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.mova.ui.screens.authentication.MovTextField
import com.example.mova.ui.theme.*
import com.example.mova.ui.screens.profile.UserViewModel
import com.example.mova.data.models.User

/**
 * Onboarding — presented once after registration to collect profile details.
 *
 * On Finish, updates the newly created user record with:
 *   height, weight, age, gender, fitnessLevel, fitnessGoal
 *
 * Then marks onboarding complete in DataStore and navigates to the main app.
 * Navigation only happens after the save succeeds (see LaunchedEffect below).
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    Log.d("OnboardingScreen", "Onboarding screen loaded")

    val uiState by viewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()
    val currentUser = userUiState.user

    // Form state — start empty. Fields get filled by the user.
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Prefer not to say") }
    var selectedFitnessLevel by remember { mutableStateOf("Beginner") }
    var fitnessGoal by remember { mutableStateOf("") }

    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Pre-fill name from session user if available
    val displayName = currentUser?.fullName ?: "there"

    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val fitnessLevelOptions = listOf("Beginner", "Intermediate", "Advanced", "Athlete")

    // Navigate only once the save succeeds
    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            Log.d("OnboardingScreen", "Save complete — navigating to app")
            onFinish()
        }
    }

    Scaffold(
        containerColor = MovaOffWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome, $displayName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MovaBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let's set up your profile so we can personalise your workouts.",
                fontSize = 14.sp,
                color = MovaGray,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MovaLightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Height + Weight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MovTextField(
                            value = height,
                            onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) height = it },
                            placeholder = "Height (cm)",
                            keyboardType = KeyboardType.Number,
                            isError = errors.containsKey("height"),
                            errorMessage = errors["height"],
                            modifier = Modifier.weight(1f)
                        )
                        MovTextField(
                            value = weight,
                            onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) weight = it },
                            placeholder = "Weight (kg)",
                            keyboardType = KeyboardType.Number,
                            isError = errors.containsKey("weight"),
                            errorMessage = errors["weight"],
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MovTextField(
                        value = age,
                        onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) age = it },
                        placeholder = "Age",
                        keyboardType = KeyboardType.Number,
                        isError = errors.containsKey("age"),
                        errorMessage = errors["age"]
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OnboardingDropdown(
                        label = "Gender",
                        options = genderOptions,
                        selected = selectedGender,
                        onSelected = { selectedGender = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OnboardingDropdown(
                        label = "Fitness Level",
                        options = fitnessLevelOptions,
                        selected = selectedFitnessLevel,
                        onSelected = { selectedFitnessLevel = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MovTextField(
                        value = fitnessGoal,
                        onValueChange = { fitnessGoal = it },
                        placeholder = "Fitness goal (e.g. Lose 5kg, run 10km)"
                    )
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "❌ ${uiState.error}",
                    color = MovaError,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        Log.d("OnboardingScreen", "Skip tapped")
                        viewModel.save(user = currentUser ?: User(), skip = true)
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Skip", color = MovaBlue, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        Log.d("OnboardingScreen", "Finish tapped")
                        val validation = validateOnboarding(height, weight, age)
                        errors = validation

                        if (validation.isEmpty() && currentUser != null) {
                            val updated = currentUser.copy(
                                height = height.toDoubleOrNull(),
                                weight = weight.toDoubleOrNull(),
                                age = age.toIntOrNull(),
                                gender = selectedGender,
                                fitnessLevel = selectedFitnessLevel,
                                fitnessGoal = fitnessGoal.ifBlank { null }
                            )
                            Log.d("OnboardingScreen", "Saving onboarding: $updated")
                            viewModel.save(user = updated)
                        } else {
                            Log.w("OnboardingScreen", "Validation errors: $validation")
                        }
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MovaBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finish Setup", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MovaBlack)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selected, color = MovaBlack)
                Text("▼", color = MovaGray, fontSize = 12.sp)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun validateOnboarding(
    height: String,
    weight: String,
    age: String
): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    if (height.isNotBlank()) {
        val h = height.toDoubleOrNull()
        if (h == null || h < 50 || h > 300) errors["height"] = "Enter 50-300 cm"
    }
    if (weight.isNotBlank()) {
        val w = weight.toDoubleOrNull()
        if (w == null || w < 10 || w > 500) errors["weight"] = "Enter 10-500 kg"
    }
    if (age.isNotBlank()) {
        val a = age.toIntOrNull()
        if (a == null || a < 1 || a > 150) errors["age"] = "Enter 1-150"
    }

    return errors
}