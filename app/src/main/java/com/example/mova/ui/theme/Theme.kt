package com.example.mova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MovaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as androidx.activity.ComponentActivity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = PrimaryLight,
            onPrimary = TextDark,
            secondary = SecondaryLight,
            onSecondary = TextDark,
            background = BackgroundDark,
            onBackground = TextLight,
            onSurface = TextLight,
            surface = Color(0xFF1E1E2E),
            surfaceVariant = Color(0xFF2A2A3C),
            onSurfaceVariant = Color(0xFFB0B0C0),
            error = AccentRed
        )
    } else {
        lightColorScheme(
            primary = Primary,
            onPrimary = TextLight,
            secondary = Secondary,
            onSecondary = TextLight,
            background = Background,
            surface = Surface,
            onBackground = TextPrimary,
            onSurface = TextPrimary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}