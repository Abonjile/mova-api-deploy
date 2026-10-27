package com.example.mova.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

/**
 * =====================================================================================
 * COMPOSABLE: LanguageSelectionScreen
 * =====================================================================================
 * Renders the language selection interface allowing users to switch between English,
 * isiZulu, and Sesotho at runtime.
 * =====================================================================================
 */
@Composable
fun LanguageSelectionScreen(
    viewModel: LanguageViewModel = viewModel()
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val languages = listOf(
        Triple("English", "en", "English"),
        Triple("isiZulu", "zu", "isiZulu"),
        Triple("Sesotho", "st", "Sesotho")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Language Settings",
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

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Select Application Language",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1A237E)
        )
        Spacer(modifier = Modifier.height(12.dp))

        languages.forEach { (displayName, code, nativeName) ->
            val isSelected = currentLanguage == code

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.changeLanguage(code) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1A1C1E)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = nativeName,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.changeLanguage(code) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD9820B))
                    )
                }
            }
        }
    }
}