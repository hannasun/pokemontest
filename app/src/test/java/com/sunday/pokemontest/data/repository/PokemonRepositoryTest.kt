package com.sunday.pokemontest.data.repository

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.sunday.pokemontest.GetPokemonDetailQuery
import com.sunday.pokemontest.SearchPokemonSpeciesQuery
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [PokemonRepository].
 *
 * Apollo is mocked via MockK — no network calls are made.
 * Each test covers one responsibility: success mapping, null color fallback,
 * empty results, GraphQL error propagation, and network failure.
 */
class PokemonRepositoryTest {

    private lateinit var apolloClient: ApolloClient
    private lateinit var repository: PokemonRepository

    //   Helpers to build minimal Apollo mock responses
    private fun mockSearchCall(response: ApolloResponse<SearchPokemonSpeciesQuery.Data>):
            ApolloCall<SearchPokemonSpeciesQuery.Data> {
        val call = mockk<ApolloCall<SearchPokemonSpeciesQuery.Data>>()
        coEvery { call.execute() } returns response
        return call
    }

    private fun mockDetailCall(response: ApolloResponse<GetPokemonDetailQuery.Data>):
            ApolloCall<GetPokemonDetailQuery.Data> {
        val call = mockk<ApolloCall<GetPokemonDetailQuery.Data>>()
        coEvery { call.execute() } returns response
        return call
    }

    @Before
    fun setUp() {
        apolloClient = mockk(relaxed = true)
        repository = PokemonRepository(apolloClient)
    }

    // searchSpecies
    @Test
    fun searchSpeciesSuccess() = runTest {
        val mockSpecies = mockk<SearchPokemonSpeciesQuery.Pokemon_v2_pokemonspecy> {
            every { id } returns 1
            every { name } returns "bulbasaur"
            every { capture_rate } returns 45
            every { pokemon_v2_pokemoncolor } returns mockk {
                every { name } returns "green"
            }
            every { pokemon_v2_pokemons } returns listOf(
                mockk {
                    every { id } returns 1
                    every { name } returns "bulbasaur"
                    every { pokemon_v2_pokemonabilities } returns listOf(
                        mockk {
                            every { pokemon_v2_ability } returns mockk { every { name } returns "overgrow" }
                        }
                    )
                }
            )
        }

        val mockAggregate = mockk<SearchPokemonSpeciesQuery.Pokemon_v2_pokemonspecies_aggregate> {
            every { aggregate } returns mockk { every { count } returns 1 }
        }

        val data = mockk<SearchPokemonSpeciesQuery.Data> {
            every { pokemon_v2_pokemonspecies } returns listOf(mockSpecies)
            every { pokemon_v2_pokemonspecies_aggregate } returns mockAggregate
        }

        val response = mockk<ApolloResponse<SearchPokemonSpeciesQuery.Data>> {
            every { hasErrors() } returns false
            every { dataOrThrow() } returns data
        }

        every { apolloClient.query(any<SearchPokemonSpeciesQuery>()) } returns mockSearchCall(
            response
        )

        val result = repository.searchSpecies("bulbasaur", 10, 0)

        assertTrue(result.isSuccess)
        val (species, total) = result.getOrThrow()
        assertEquals(1, total)
        assertEquals(1, species.size)
        with(species.first()) {
            assertEquals(1, id)
            assertEquals("bulbasaur", name)
            assertEquals(45, captureRate)
            assertEquals("green", colorName)
            assertEquals(1, pokemons.size)
            assertEquals("overgrow", pokemons.first().abilities.first())
        }
    }

    @Test
    fun searchSpeciesReturnsEmptyListWhenNoResults() = runTest {
        val data = mockk<SearchPokemonSpeciesQuery.Data> {
            every { pokemon_v2_pokemonspecies } returns emptyList()
            every { pokemon_v2_pokemonspecies_aggregate } returns mockk {
                every { aggregate } returns mockk { every { count } returns 0 }
            }
        }

        val response = mockk<ApolloResponse<SearchPokemonSpeciesQuery.Data>> {
            every { hasErrors() } returns false
            every { dataOrThrow() } returns data
        }

        every { apolloClient.query(any<SearchPokemonSpeciesQuery>()) } returns mockSearchCall(
            response
        )

        val result = repository.searchSpecies("xyznotapokemon", 10, 0)
        assertTrue(result.isSuccess)
        val (species, total) = result.getOrThrow()
        assertEquals(0, total)
        assertTrue(species.isEmpty())
    }

    @Test
    fun searchSpeciesReturnsFailureWhenGraphQLReturnsErrors() = runTest {
        val response = mockk<ApolloResponse<SearchPokemonSpeciesQuery.Data>> {
            every { hasErrors() } returns true
        }

        every { apolloClient.query(any<SearchPokemonSpeciesQuery>()) } returns mockSearchCall(
            response
        )

        val result = repository.searchSpecies("bulbasaur", 10, 0)
        assertTrue(result.isFailure)
    }

    @Test
    fun searchSpeciesReturnFailureOnNetworkException() = runTest {
        val call = mockk<ApolloCall<SearchPokemonSpeciesQuery.Data>>()
        coEvery { call.execute() } throws RuntimeException("No internet")
        every { apolloClient.query(any<SearchPokemonSpeciesQuery>()) } returns call

        val result = repository.searchSpecies("bulbasaur", 10, 0)
        assertTrue(result.isFailure)
        assertEquals("No internet", result.exceptionOrNull()?.message)
    }

    //getSpeciesDetail

    @Test
    fun detailSuccess() = runTest {
        val mockPokemon = mockk<GetPokemonDetailQuery.Pokemon_v2_pokemon> {
            every { id } returns 1
            every { name } returns "bulbasaur"
            every { pokemon_v2_pokemonabilities } returns listOf(
                mockk { every { pokemon_v2_ability } returns mockk { every { name } returns "overgrow" } },
                mockk { every { pokemon_v2_ability } returns mockk { every { name } returns "chlorophyll" } }
            )
        }

        val mockSpecies = mockk<GetPokemonDetailQuery.Pokemon_v2_pokemonspecies_by_pk> {
            every { id } returns 1
            every { name } returns "bulbasaur"
            every { capture_rate } returns 45
            every { pokemon_v2_pokemoncolor } returns mockk { every { name } returns "green" }
            every { pokemon_v2_pokemons } returns listOf(mockPokemon)
        }

        val data = mockk<GetPokemonDetailQuery.Data> {
            every { pokemon_v2_pokemonspecies_by_pk } returns mockSpecies
        }

        val response = mockk<ApolloResponse<GetPokemonDetailQuery.Data>> {
            every { hasErrors() } returns false
            every { dataOrThrow() } returns data
        }

        every { apolloClient.query(any<GetPokemonDetailQuery>()) } returns mockDetailCall(response)

        val result = repository.getSpeciesDetail(1)
        assertTrue(result.isSuccess)
        val species = result.getOrThrow()!!
        assertEquals("bulbasaur", species.name)
        assertEquals(listOf("overgrow", "chlorophyll"), species.pokemons.first().abilities)
    }

    @Test
    fun detailReturnNullWhenSpeciesNotFound() = runTest {
        val data = mockk<GetPokemonDetailQuery.Data> {
            every { pokemon_v2_pokemonspecies_by_pk } returns null
        }

        val response = mockk<ApolloResponse<GetPokemonDetailQuery.Data>> {
            every { hasErrors() } returns false
            every { dataOrThrow() } returns data
        }

        every { apolloClient.query(any<GetPokemonDetailQuery>()) } returns mockDetailCall(response)

        val result = repository.getSpeciesDetail(9999)
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun detailFailureOnGraphQLError() = runTest {
        val response = mockk<ApolloResponse<GetPokemonDetailQuery.Data>> {
            every { hasErrors() } returns true
        }

        every { apolloClient.query(any<GetPokemonDetailQuery>()) } returns mockDetailCall(response)

        val result = repository.getSpeciesDetail(1)
        assertTrue(result.isFailure)
    }
}
