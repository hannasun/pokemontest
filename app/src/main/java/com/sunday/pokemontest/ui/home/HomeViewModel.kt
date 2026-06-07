package com.sunday.pokemontest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.data.paging.PokemonPageSource
import com.sunday.pokemontest.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            // Safety timeout: if loading takes more than 3 seconds, hide the splash screen anyway
            delay(3000)
            _isLoading.value = false
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val pokemonSpecies: Flow<PagingData<PokemonSpecies>> = _searchQuery
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { query ->//Get the last query result
            Pager(
                config = PagingConfig(
                    pageSize = PokemonPageSource.PAGE_SIZE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    PokemonPageSource(repository, query)
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onLoadingFinished() {
        _isLoading.value = false
    }
}