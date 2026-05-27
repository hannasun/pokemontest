package com.sunday.pokemontest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sunday.pokemontest.R
import com.sunday.pokemontest.data.PokemonSpecies
import com.sunday.pokemontest.ui.theme.PokemonBlue
import com.sunday.pokemontest.ui.theme.PokemonYellow
import com.sunday.pokemontest.ui.theme.pokemonColorToCompose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), onSpeciesClicked: (Int) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val lazyItems = viewModel.pokemonSpecies.collectAsLazyPagingItems()
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                        color = PokemonYellow,
                        fontSize = 22.sp,
                        letterSpacing = 1.sp
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PokemonBlue)
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //Search box

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text(stringResource(R.string.home_search_hint)) },
                shape = RoundedCornerShape(50),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = PokemonBlue,
                    cursorColor = PokemonBlue
                )
            )
            //Loading or Error or List
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    lazyItems.loadState.refresh is LoadState.Loading -> {
                        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    lazyItems.loadState.refresh is LoadState.Error -> {
                        val e = (lazyItems.loadState.refresh as LoadState.Error).error
                        Text(
                            text = "Error: ${e.message}",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = 14.dp, vertical = 8.dp
                            ), verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                count = lazyItems.itemCount,
                                key = lazyItems.itemKey { it.id }) { idx ->
                                lazyItems[idx]?.let { species ->
                                    SpeciesCard(
                                        species = species,
                                        onClick = { onSpeciesClicked(species.id) })
                                }
                            }
                            //Append loading indicator
                            if (lazyItems.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = PokemonBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

//@Preview
@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = PokemonBlue,
            trackColor = PokemonYellow,
            modifier = Modifier.size(56.dp),
            strokeWidth = 5.dp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.home_loading_pokemon),
            color = PokemonBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SpeciesCard(species: PokemonSpecies, onClick: () -> Unit) {
    val bgColor = pokemonColorToCompose(species.colorName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = species.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = PokemonBlue,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Capture Rate: ${species.captureRate ?: "?"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokemonBlue.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.height(10.dp))
            // Pokémon chips
            Text(
                text = stringResource(R.string.home_list_item_pokmons_in_species),
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))
            SpeciesRows(species.pokemons.map { it.name })
        }
    }
}

@Composable
private fun SpeciesRows(names: List<String>) {
    val chunks = names.chunked(3)
    chunks.forEach { row ->
        Row(modifier = Modifier.padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            row.forEach { name ->
                PokemonChunk(name)
            }
        }
    }
}

@Composable
private fun PokemonChunk(name: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PokemonBlue.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            color = PokemonBlue,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}