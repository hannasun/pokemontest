package com.sunday.pokemontest.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokemon.app.ui.component.PokemonCircularProgressIndicator
import com.sunday.pokemontest.R
import com.sunday.pokemontest.data.PokemonSpecies
import com.sunday.pokemontest.ui.component.PokemonItemCard
import com.sunday.pokemontest.ui.component.PokemonLoadingErrorText
import com.sunday.pokemontest.ui.theme.PokemonBlue
import com.sunday.pokemontest.ui.theme.PokemonBlueDark
import com.sunday.pokemontest.ui.theme.PokemonYellow
import com.sunday.pokemontest.ui.theme.pokemonColorToCompose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(), onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState is DetailUiState.Success) (uiState as DetailUiState.Success).species.name else "Detail",
                        fontWeight = FontWeight.ExtraBold,
                        color = PokemonYellow,
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text(
                            text = "←-",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PokemonBlue)
            )
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    PokemonCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DetailUiState.Error -> {
                    PokemonLoadingErrorText(
                        state.message, Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                is DetailUiState.Success -> {
                    SpeciesDetail(species = state.species)
                }
            }
        }
    }
}

@Composable
fun SpeciesDetail(species: PokemonSpecies) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PokemonBlue, PokemonYellow)
                    )
                ), contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = species.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.detail_capture_rate, species.captureRate ?: "—"),
                    color = PokemonBlueDark,
                    fontSize = 14.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            species.pokemons.forEach { pokemon ->
                PokemonItemCard(
                    title = pokemon.name,
                    subtitle = "Abilities",
                    bgColor = pokemonColorToCompose(species.colorName),
                    captureRate = "",
                    data = pokemon.abilities
                ) {

                }
            }
        }
    }
}