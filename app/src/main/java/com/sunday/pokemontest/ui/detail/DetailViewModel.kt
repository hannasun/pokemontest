package com.sunday.pokemontest.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val species: PokemonSpecies) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val repository: PokemonRepository
) : ViewModel() {

    private val speciesId: Int = checkNotNull(savedStateHandle["speciesId"])
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            repository.getSpeciesDetail(speciesId).fold(
                onSuccess = { species ->
                    _uiState.value = if (species != null)
                        DetailUiState.Success(species)
                    else
                        DetailUiState.Error("Species Not Found")
                }, onFailure = {
                    _uiState.value = DetailUiState.Error(it.message ?: "Unknown Error")
                })
        }
    }

    fun retry() {
        loadDetail()
    }
}