package com.sunday.pokemontest.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sunday.pokemontest.ui.component.PokemonCircularProgressIndicator
import com.sunday.pokemontest.R
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.ui.component.PokemonItemCard
import com.sunday.pokemontest.ui.component.PokemonLoadingErrorText
import com.sunday.pokemontest.ui.theme.PokemonBlue
import com.sunday.pokemontest.ui.theme.PokemonYellow
import com.sunday.pokemontest.ui.theme.pokemonColorToCompose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onSpeciesClicked: (Int) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val lazyItems = viewModel.pokemonSpecies.collectAsLazyPagingItems()
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(lazyItems.loadState.refresh) {
        // Dismiss splash screen if we have finished loading or encountered an error
        if (lazyItems.loadState.refresh is LoadState.NotLoading ||
            lazyItems.loadState.refresh is LoadState.Error
        ) {
            viewModel.onLoadingFinished()
        }
    }

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
                when (lazyItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is LoadState.Error -> {
                        val e = (lazyItems.loadState.refresh as LoadState.Error).error
                        PokemonLoadingErrorText(
                            error = e.message ?: stringResource(R.string.loading_unknow_error),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        ) {
                            lazyItems.retry()
                        }
                    }

                    else -> {
                        if (lazyItems.itemCount == 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.search_not_found),
                                    textAlign = TextAlign.Center,
                                    color = PokemonBlue,
                                    fontSize = 16.sp,
                                )
                            }
                        } else {
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
                                            PokemonCircularProgressIndicator()
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
}

//@Preview
@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PokemonCircularProgressIndicator(Modifier.size(56.dp))
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
    PokemonItemCard(
        imageUrl = species.imageUrl,
        title = species.name,
        subtitle = stringResource(R.string.home_list_item_pokmons_in_species),
        bgColor = bgColor,
        captureRate = species.captureRate?.toString() ?: "",
        data = species.pokemons.map { it.name },
        onClick = onClick
    )
}
