package com.sunday.pokemontest

import com.sunday.pokemontest.data.Pokemon
import com.sunday.pokemontest.data.PokemonSpecies

/**
 * Central place for all fake domain objects used across unit tests.
 * Keeps test code DRY and makes intention clear.
 */
object TestData {

    val bulbasaurAbilities = listOf("overgrow", "chlorophyll")
    val charmanderAbilities = listOf("blaze", "solar-power")

    val bulbasaur = Pokemon(id = 1, name = "bulbasaur", abilities = bulbasaurAbilities)
    val ivysaur   = Pokemon(id = 2, name = "ivysaur",   abilities = listOf("overgrow", "chlorophyll"))
    val charmander = Pokemon(id = 4, name = "charmander", abilities = charmanderAbilities)

    val bulbasaurSpecies = PokemonSpecies(
        id          = 1,
        name        = "bulbasaur",
        captureRate = 45,
        colorName   = "green",
        pokemons    = listOf(bulbasaur, ivysaur)
    )

    val charmanderSpecies = PokemonSpecies(
        id = 4,
        name = "charmander",
        captureRate = 45,
        colorName = "red",
        pokemons = listOf(charmander)
    )

    val pikachuSpecies = PokemonSpecies(
        id          = 25,
        name        = "pikachu",
        captureRate = 190,
        colorName   = "yellow",
        pokemons    = listOf(
            Pokemon(id = 25, name = "pikachu", abilities = listOf("static", "lightning-rod"))
        )
    )

    val nullColorSpecies = PokemonSpecies(
        id          = 999,
        name        = "missingno",
        captureRate = null,
        colorName   = "blue",   // default fallback
        pokemons    = emptyList()
    )

    /** A full page of species (10 items) for pagination tests. */
    val fullPage: List<PokemonSpecies> = (1..10).map { i ->
        PokemonSpecies(
            id          = i,
            name        = "species-$i",
            captureRate = i * 10,
            colorName   = "blue",
            pokemons    = emptyList()
        )
    }

    /** A partial last page (3 items) for pagination boundary tests. */
    val partialPage: List<PokemonSpecies> = (11..13).map { i ->
        PokemonSpecies(
            id          = i,
            name        = "species-$i",
            captureRate = i * 10,
            colorName   = "blue",
            pokemons    = emptyList()
        )
    }
}
