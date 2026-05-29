package com.sunday.pokemontest.domain.mapper

import com.sunday.pokemontest.GetPokemonDetailQuery
import com.sunday.pokemontest.SearchPokemonSpeciesQuery
import com.sunday.pokemontest.common.extension.toPokemonImageUrl
import com.sunday.pokemontest.domain.model.Pokemon
import com.sunday.pokemontest.domain.model.PokemonSpecies

// graphql data class to model
fun SearchPokemonSpeciesQuery.Pokemon_v2_pokemonspecy.toDomain() =
    PokemonSpecies(
        id = id,
        name = name,
        captureRate = capture_rate,
        colorName = pokemon_v2_pokemoncolor?.name ?: "blue",
        imageUrl = id.toPokemonImageUrl(),
        pokemons = pokemon_v2_pokemons.map { it.toDomainSearch() }
    )

fun SearchPokemonSpeciesQuery.Pokemon_v2_pokemon.toDomainSearch() =
    Pokemon(
        id = id,
        name = name,
        imageUrl = id.toPokemonImageUrl(),
        abilities = pokemon_v2_pokemonabilities.mapNotNull {
            it.pokemon_v2_ability?.name
        }
    )

fun GetPokemonDetailQuery.Pokemon_v2_pokemonspecies_by_pk.toDomain() =
    PokemonSpecies(
        id = id,
        name = name,
        captureRate = capture_rate,
        colorName = pokemon_v2_pokemoncolor?.name ?: "blue",
        imageUrl = id.toPokemonImageUrl(),
        pokemons = pokemon_v2_pokemons.map { it.toDomainDetail() }
    )

fun GetPokemonDetailQuery.Pokemon_v2_pokemon.toDomainDetail() =
    Pokemon(
        id = id,
        name = name,
        imageUrl = id.toPokemonImageUrl(),
        abilities = pokemon_v2_pokemonabilities.mapNotNull {
            it.pokemon_v2_ability?.name
        }
    )