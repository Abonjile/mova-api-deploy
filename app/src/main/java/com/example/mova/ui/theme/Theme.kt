package com.example.mova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
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
            primary = Primary,
            onPrimary = TextLight,
            secondary = Secondary,
            onSecondary = TextLight,
            background = BackgroundDark,
            surface = SecondaryDark,
            onBackground = TextLight,
            onSurface = TextLight
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