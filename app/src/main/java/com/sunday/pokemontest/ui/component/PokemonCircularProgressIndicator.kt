package com.pokemon.app.ui.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sunday.pokemontest.ui.theme.PokemonBlue
import com.sunday.pokemontest.ui.theme.PokemonYellow

@Composable
fun PokemonCircularProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = PokemonBlue,
        trackColor = PokemonYellow,
        modifier = modifier,
        strokeWidth = 5.dp
    )
}