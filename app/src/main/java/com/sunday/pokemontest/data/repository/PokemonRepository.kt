package com.sunday.pokemontest.data.repository

import com.apollographql.apollo.ApolloClient
import com.sunday.pokemontest.GetPokemonDetailQuery
import com.sunday.pokemontest.SearchPokemonSpeciesQuery
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.domain.mapper.toDomain
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
        val searchTerm = "${query.trim().lowercase()}%"
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
}