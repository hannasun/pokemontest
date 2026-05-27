package com.sunday.pokemontest

import android.R.color.transparent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sunday.pokemontest.ui.theme.PokemonTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setBackgroundDrawableResource(transparent)
        setContent {
            PokemonTestTheme {
                PokemonNavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonPreview() {
    PokemonTestTheme {
        PokemonNavGraph()
    }
}