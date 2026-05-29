package com.sunday.pokemontest.domain.model

data class PokemonSpecies(
    val id: Int,
    val name: String,
    val captureRate: Int?,
    val colorName: String,
    val imageUrl:String,
    val pokemons: List<Pokemon>
)

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl:String,
    val abilities: List<String>
)
