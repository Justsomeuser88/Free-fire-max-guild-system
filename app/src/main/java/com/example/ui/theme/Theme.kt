package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FFGamingColorScheme = darkColorScheme(
    primary = FFFireOrange,
    onPrimary = FFTextPrimary,
    primaryContainer = FFFireOrangeLight,
    onPrimaryContainer = FFDarkBackground,
    secondary = FFFireGold,
    onSecondary = FFDarkBackground,
    secondaryContainer = FFDarkSurfaceVariant,
    onSecondaryContainer = FFTextGold,
    tertiary = FFCyanAccent,
    onTertiary = FFDarkBackground,
    background = FFDarkBackground,
    onBackground = FFTextPrimary,
    surface = FFDarkSurface,
    onSurface = FFTextPrimary,
    surfaceVariant = FFDarkSurfaceVariant,
    onSurfaceVariant = FFTextSecondary,
    surfaceContainer = FFDarkSurfaceCard,
    outline = FFDarkBorder,
    error = FFFireRed,
    onError = FFTextPrimary
)

@Composable
fun FreeFireGuildTheme(
    darkTheme: Boolean = true, // We default to gaming dark theme
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = FFDarkBackground.toArgb()
            window.navigationBarColor = FFDarkBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = FFGamingColorScheme,
        typography = Typography,
        content = content
    )
}
