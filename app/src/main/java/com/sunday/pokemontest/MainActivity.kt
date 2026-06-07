package com.sunday.pokemontest

import android.R.color.transparent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sunday.pokemontest.ui.home.HomeViewModel
import com.sunday.pokemontest.ui.theme.PokemonTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val startTime = System.currentTimeMillis()
        splashScreen.setKeepOnScreenCondition {
            val isStillLoading = viewModel.isLoading.value
            val hasTimedOut = System.currentTimeMillis() - startTime > 4000
            isStillLoading && !hasTimedOut
        }

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