package com.sunday.pokemontest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



val PokemonYellow = Color(0xFFFFCB05)
val PokemonBlue = Color(0xFF3B5BA8)
val PokemonBlueDark = Color(0xFF2A4080)
val SurfaceLight = Color(0xFFF8F4E8)
val OnSurface = Color(0xFF1A1A2E)

// Pokémon color map
fun pokemonColorToCompose(colorName: String): Color = when (colorName.lowercase()) {
    "red" -> Color(0xFFE74C3C)
    "blue" -> Color(0xFF3498DB)
    "yellow" -> Color(0xFFF1C40F)
    "green" -> Color(0xFF2ECC71)
    "black" -> Color(0xFF2C3E50)
    "brown" -> Color(0xFF8D6E63)
    "purple" -> Color(0xFF9B59B6)
    "gray" -> Color(0xFF95A5A6)
    "white" -> Color(0xFFECF0F1)
    "pink" -> Color(0xFFFF8FAB)
    else -> PokemonBlue
}

private val PokemonColorScheme = lightColorScheme(
    primary = PokemonBlue,
    onPrimary = Color.White,
    primaryContainer = PokemonYellow,
    onPrimaryContainer = OnSurface,
    secondary = PokemonYellow,
    onSecondary = OnSurface,
    background = SurfaceLight,
    onBackground = OnSurface,
    surface = Color.White,
    onSurface = OnSurface,
)

@Composable
fun PokemonTestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PokemonColorScheme,
        typography = Typography,
        content = content
    )
}