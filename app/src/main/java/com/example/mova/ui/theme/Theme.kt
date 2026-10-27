package com.example.mova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

@Composable
fun MovaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    largeText: Boolean = false,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as androidx.activity.ComponentActivity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    val colorScheme = when {
        // Extreme high contrast mode (it's pure black/white, no subtle greys)
        highContrast && darkTheme -> darkColorScheme(
            primary = Color.Yellow,  // High visibility accent
            onPrimary = Color.Black,
            secondary = Color.Cyan,
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White,
            surfaceVariant = Color.Black,
            onSurfaceVariant = Color.White,
            error = Color(0xFFFF6B6B),
            onError = Color.Black,
            outline = Color.White
        )

        highContrast && !darkTheme -> lightColorScheme(
            primary = Color.Blue,  // High visibility accent
            onPrimary = Color.White,
            secondary = Color.Magenta,
            onSecondary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            surfaceVariant = Color.White,
            onSurfaceVariant = Color.Black,
            error = Color(0xFFB00020),
            onError = Color.White,
            outline = Color.Black
        )

        // Standard dark mode
        darkTheme -> darkColorScheme(
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

        // Standard light mode
        else -> lightColorScheme(
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

    // Scale typography if largeText is true
    val finalTypography = if (largeText) {
        Typography(
            bodyLarge = Typography().bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
            bodyMedium = Typography().bodyMedium.copy(fontSize = 18.sp, lineHeight = 24.sp),
            titleLarge = Typography().titleLarge.copy(fontSize = 24.sp),
        )
    } else {
        Typography // default
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = finalTypography,
        content = content
    )
}