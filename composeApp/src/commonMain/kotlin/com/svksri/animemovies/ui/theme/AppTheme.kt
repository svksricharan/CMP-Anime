package com.svksri.animemovies.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    Light,
    Dark
}

private val LightColors = lightColorScheme(
    primary = SakuraPurple,
    secondary = SakuraPink,
    background = SoftBackground,
    surface = SoftSurface,
    surfaceVariant = SoftSurfaceVariant,
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1622),
    onSurface = Color(0xFF1D1B20)
)

private val DarkColors = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonPink,
    background = NightBackground,
    surface = NightSurface,
    surfaceVariant = NightSurfaceVariant,
    onPrimary = Color(0xFF240B46),
    onBackground = Color(0xFFEEE7F5),
    onSurface = Color(0xFFF3EDF7)
)

@Composable
fun AnimeMoviesTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when (themeMode) {
            ThemeMode.Light -> LightColors
            ThemeMode.Dark -> DarkColors
        },
        content = content
    )
}
