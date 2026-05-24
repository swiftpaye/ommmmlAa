package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    secondary = MintJade,
    tertiary = GlowingPurple,
    background = DarkBg,
    surface = SurfaceDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = Color(0x286366F1),
    secondaryContainer = Color(0x2810B981)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
