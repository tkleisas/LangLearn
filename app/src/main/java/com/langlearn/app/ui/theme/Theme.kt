package com.langlearn.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TealPrimary = Color(0xFF00897B)
val TealOnPrimary = Color(0xFFFFFFFF)
val TealPrimaryContainer = Color(0xFFB2DFDB)
val TealOnPrimaryContainer = Color(0xFF00332C)

val AmberSecondary = Color(0xFFFF6F00)
val AmberOnSecondary = Color(0xFFFFFFFF)
val AmberSecondaryContainer = Color(0xFFFFE0B2)
val AmberOnSecondaryContainer = Color(0xFF3A1A00)

val CreamSurface = Color(0xFFFFF8E1)
val CreamOnSurface = Color(0xFF1E1B16)
val CreamSurfaceVariant = Color(0xFFE7E0CE)
val CreamOnSurfaceVariant = Color(0xFF4A463A)

val WhiteBackground = Color(0xFFFFFFFF)
val CreamOnBackground = Color(0xFF1E1B16)

val WarmError = Color(0xFFBA1A1A)
val WarmOnError = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = AmberSecondary,
    onSecondary = AmberOnSecondary,
    secondaryContainer = AmberSecondaryContainer,
    onSecondaryContainer = AmberOnSecondaryContainer,
    surface = CreamSurface,
    onSurface = CreamOnSurface,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = CreamOnSurfaceVariant,
    background = WhiteBackground,
    onBackground = CreamOnBackground,
    error = WarmError,
    onError = WarmOnError,
)

private val DarkTealPrimary = Color(0xFF80CBC4)
private val DarkTealOnPrimary = Color(0xFF00332C)
private val DarkTealPrimaryContainer = Color(0xFF00695C)
private val DarkTealOnPrimaryContainer = Color(0xFFB2DFDB)

private val DarkAmberSecondary = Color(0xFFFFB74D)
private val DarkAmberOnSecondary = Color(0xFF3A1A00)
private val DarkAmberSecondaryContainer = Color(0xFFCC5800)
private val DarkAmberOnSecondaryContainer = Color(0xFFFFE0B2)

private val DarkSurface = Color(0xFF1A1A18)
private val DarkOnSurface = Color(0xFFE6E2D6)
private val DarkSurfaceVariant = Color(0xFF3D3A30)
private val DarkOnSurfaceVariant = Color(0xFFCBC8B5)

private val DarkBackground = Color(0xFF121210)
private val DarkOnBackground = Color(0xFFE6E2D6)

private val DarkError = Color(0xFFFFB4AB)
private val DarkOnError = Color(0xFF690005)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTealPrimary,
    onPrimary = DarkTealOnPrimary,
    primaryContainer = DarkTealPrimaryContainer,
    onPrimaryContainer = DarkTealOnPrimaryContainer,
    secondary = DarkAmberSecondary,
    onSecondary = DarkAmberOnSecondary,
    secondaryContainer = DarkAmberSecondaryContainer,
    onSecondaryContainer = DarkAmberOnSecondaryContainer,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    error = DarkError,
    onError = DarkOnError,
)

@Composable
fun LangLearnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
