package com.sunday.pokemontest.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PokemonLoadingErrorText(error: String, modifier: Modifier = Modifier) {
    Text(
        text = "Error: $error",
        modifier = modifier,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}
