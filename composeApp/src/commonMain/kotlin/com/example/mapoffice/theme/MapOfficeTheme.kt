package com.example.mapoffice.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val LightColorTheme = lightColorScheme(
    primary = Blue,               // --primary, --app-accent
    onPrimary = PWhite,           // --on-primary

    secondary = XlBlue,           // --inactive (Thay thế cho --secondary đang trống)
    onSecondary = PWhite,

    primaryContainer = LightBlue, // --active
    onPrimaryContainer = PWhite,  // --on-active

    background = XlGrey,          // --background
    onBackground = Black,         // --on-background

    surface = White,              // --surface-background, --textual-background
    onSurface = Black,            // --on-surface

    surfaceVariant = LightGrey,   // --control-background
    onSurfaceVariant = DarkGrey,  // --text-secondary

    error = Red,                  // --error
    onError = White,              // --on-error

    outline = DarkGrey,           // --border-control
    outlineVariant = LightGrey    // --border-surface
)


@Composable
fun extendedColor(light: Color, dark: Color): Color {
    return if (isSystemInDarkTheme()) dark else light
}

val ColorScheme.extraColor: Color
    @Composable get() = extendedColor(
        light = Color(0xFF000000),
        dark = Color(0xFFFFFFFF)
    )

val Shapes = Shapes(
    large = RoundedCornerShape(28.dp),
    medium = RoundedCornerShape(16.dp)
)

@Composable
fun MapOfficeTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorTheme,
        // typography = Typography,
        shapes = Shapes,
        content = content
    )
}