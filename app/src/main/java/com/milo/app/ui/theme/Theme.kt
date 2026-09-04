package com.milo.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MiloWhite,
    onPrimary = MiloBlack,
    secondary = MiloZinc200,
    onSecondary = MiloBlack,
    background = MiloBlack,
    onBackground = MiloWhite,
    surface = MiloCardDark,
    onSurface = MiloWhite,
    surfaceVariant = MiloBorderDark,
    onSurfaceVariant = MiloZinc400
)

private val LightColorScheme = lightColorScheme(
    primary = MiloBlack,
    onPrimary = MiloWhite,
    secondary = MiloZinc800,
    onSecondary = MiloWhite,
    background = MiloWhite,
    onBackground = MiloBlack,
    surface = MiloCardLight,
    onSurface = MiloBlack,
    surfaceVariant = MiloBorderLight,
    onSurfaceVariant = MiloZinc500
)

@Composable
fun MiloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MiloTypography,
        shapes = MiloShapes,
        content = content
    )
}
