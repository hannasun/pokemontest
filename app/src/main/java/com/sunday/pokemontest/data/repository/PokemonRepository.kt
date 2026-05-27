package com.sunday.pokemontest.data.repository

import com.apollographql.apollo.ApolloClient
import com.sunday.pokemontest.GetPokemonDetailQuery
import com.sunday.pokemontest.SearchPokemonSpeciesQuery
import com.sunday.pokemontest.data.Pokemon
import com.sunday.pokemontest.data.PokemonSpecies
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun searchSpecies(
        query: String,
        limit: Int,
        offset: Int
    ): Result<Pair<List<PokemonSpecies>, Int>> = runCatching {
        val searchTerm = "%${query.trim()}%"
        val response = apolloClient
            .query(
                SearchPokemonSpeciesQuery(
                    search = searchTerm,
                    limit = limit,
                    offset = offset
                )
            )
            .execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "GraphQL error")
        }

        val data = response.dataOrThrow()
        val total = data.pokemon_v2_pokemonspecies_aggregate.aggregate?.count ?: 0
        val species = data.pokemon_v2_pokemonspecies.map { it.toDomain() }
        species to total
    }

    suspend fun getSpeciesDetail(id: Int): Result<PokemonSpecies?> = runCatching {
        val response = apolloClient
            .query(GetPokemonDetailQuery(id = id))
            .execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "GraphQL error")
        }

        response.dataOrThrow().pokemon_v2_pokemonspecies_by_pk?.toDomain()
    }

    // graphql data class to model
    private fun SearchPokemonSpeciesQuery.Pokemon_v2_pokemonspecy.toDomain() =
        PokemonSpecies(
            id = id,
            name = name,
            captureRate = capture_rate,
            colorName = pokemon_v2_pokemoncolor?.name ?: "blue",
            pokemons = pokemon_v2_pokemons.map { it.toDomainSearch() }
        )

    private fun SearchPokemonSpeciesQuery.Pokemon_v2_pokemon.toDomainSearch() =
        Pokemon(
            id = id,
            name = name,
            abilities = pokemon_v2_pokemonabilities.mapNotNull {
                it.pokemon_v2_ability?.name
            }
        )

    private fun GetPokemonDetailQuery.Pokemon_v2_pokemonspecies_by_pk.toDomain() =
        PokemonSpecies(
            id = id,
            name = name,
            captureRate = capture_rate,
            colorName = pokemon_v2_pokemoncolor?.name ?: "blue",
            pokemons = pokemon_v2_pokemons.map { it.toDomainDetail() }
        )

    private fun GetPokemonDetailQuery.Pokemon_v2_pokemon.toDomainDetail() =
        Pokemon(
            id = id,
            name = name,
            abilities = pokemon_v2_pokemonabilities.mapNotNull {
                it.pokemon_v2_ability?.name
            }
        )
}