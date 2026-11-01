package com.example.mova.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mova.R

/**
 * COMPOSABLE: LanguageSelectionScreen
 * Lets the user pick English, isiZulu, Sesotho or the device language. Every label comes
 * from strings.xml, so the screen itself translates when the language changes.
 */
@Composable
fun LanguageSelectionScreen(
    userId: String,
    viewModel: LanguageViewModel = viewModel()
) {
    val selected by viewModel.selected.collectAsState()

    // Pairs of language tag and the string resource that names it.
    val options = listOf(
        LanguageManager.ENGLISH to R.string.language_english,
        LanguageManager.ZULU to R.string.language_zulu,
        LanguageManager.SESOTHO to R.string.language_sesotho,
        LanguageManager.SYSTEM to R.string.language_use_device
    )
    val currentNameRes = options.firstOrNull { it.first == selected }?.second
        ?: R.string.language_use_device

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.language_screen_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.language_welcome),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            text = stringResource(R.string.language_description),
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.language_choose_label),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1A237E)
        )

        options.forEach { (tag, labelRes) ->
            val isSelected = selected == tag
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { viewModel.select(tag) },
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
                    Text(
                        text = stringResource(labelRes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.select(tag) },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD9820B))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(
                R.string.language_current_label,
                stringResource(currentNameRes)
            ),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}