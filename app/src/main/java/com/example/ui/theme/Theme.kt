package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val HighDensityColorScheme = darkColorScheme(
    primary = HighDensityPrimary,
    onPrimary = HighDensityOnPrimary,
    primaryContainer = HighDensitySecondaryContainer,
    onPrimaryContainer = HighDensityOnSecondaryContainer,
    secondary = HighDensityPrimary,
    onSecondary = HighDensityOnPrimary,
    secondaryContainer = HighDensitySecondaryContainer,
    onSecondaryContainer = HighDensityOnSecondaryContainer,
    tertiary = HighDensityTertiaryPink,
    background = HighDensityBackground,
    onBackground = HighDensityTextPrimary,
    surface = HighDensitySurface,
    onSurface = HighDensityTextPrimary,
    surfaceVariant = HighDensitySurface,
    onSurfaceVariant = HighDensityTextSecondary,
    outline = HighDensitySurfaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark theme for premium High Density Slate layout
    dynamicColor: Boolean = false, // Disable dynamic colors so our High Density branding is clean and consistent
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = HighDensityColorScheme,
        typography = Typography,
        content = content
    )
}
