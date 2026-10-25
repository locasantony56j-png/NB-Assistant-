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

private val DarkColorScheme = darkColorScheme(
    primary = NbGreenPrimary,
    onPrimary = Color.Black,
    primaryContainer = NbGreenDark,
    onPrimaryContainer = Color.White,
    secondary = NbGreenGlow,
    onSecondary = Color.Black,
    secondaryContainer = NbBlackSurfaceVariant,
    onSecondaryContainer = NbGreenGlow,
    tertiary = NbInfo,
    background = NbBlackDark,
    onBackground = Color(0xFFF0F6FC),
    surface = NbBlackSurface,
    onSurface = Color(0xFFF0F6FC),
    surfaceVariant = NbBlackSurfaceVariant,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = NbBlackOutline,
    error = NbError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NbGreenDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F8E8),
    onPrimaryContainer = Color(0xFF00381B),
    secondary = NbGreenPrimary,
    onSecondary = Color.Black,
    background = NbLightBg,
    onBackground = Color(0xFF1E293B),
    surface = NbLightSurface,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = NbLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    error = NbError,
    onError = Color.White
)

@Composable
fun NbAssistantTheme(
    darkTheme: Boolean = true, // Default to sleek futuristic dark theme matching logo
    dynamicColor: Boolean = false, // Keep signature green & black branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
