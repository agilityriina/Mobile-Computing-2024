package com.rannunen.assistedreminder2023.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPaletteOld = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)
private val DarkColorPalette = darkColors(
    primary = pink100,
    primaryVariant = pink900,
    secondary = pink50
)

private val LightColorPaletteOld = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

)
private val LightColorPalette = lightColors(
    primary = pink100,
    primaryVariant = pink900,
    secondary = pink50

)

@Composable
fun AssistedReminder2023Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}