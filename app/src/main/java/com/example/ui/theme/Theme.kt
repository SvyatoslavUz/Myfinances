package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Elegant Dark Color Scheme based on HTML design tokens
private val ElegantDarkColorScheme = darkColorScheme(
    primary = LilacPrimary,
    onPrimary = LilacOnPrimary,
    primaryContainer = LilacPrimaryContainer,
    onPrimaryContainer = LilacOnPrimaryContainer,
    secondary = SoftRose,
    onSecondary = SoftRoseOn,
    secondaryContainer = SoftRoseContainer,
    onSecondaryContainer = SoftRose,
    tertiary = Color(0xFFCCC2DC),
    onTertiary = Color(0xFF332D41),
    background = ElegantDarkCanvas,
    surface = ElegantDarkSurface,
    surfaceVariant = ElegantDarkVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = ElegantDarkOutline,
    outlineVariant = ElegantDarkVariant
)

// Fallback Light Theme that gracefully aligns with the lilac/rose accents
private val ElegantLightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = LilacPill,
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF7D5260),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF31111D),
    tertiary = Color(0xFF7D5260),
    background = Color(0xFFFEF7FF),
    surface = Color(0xFFFEF7FF),
    surfaceVariant = Color(0xFFE7E0EC),
    onBackground = Color(0xFF1D1B20),
    onSurface = Color(0xFF1D1B20),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Elegant Dark theme
    dynamicColor: Boolean = false, // Preserve our handcrafted Elegant Dark styling
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ElegantDarkColorScheme else ElegantLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

