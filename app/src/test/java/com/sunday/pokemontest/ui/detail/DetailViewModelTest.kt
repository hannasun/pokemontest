package com.sunday.pokemontest.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sunday.pokemontest.TestData
import com.sunday.pokemontest.data.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val instantTask = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PokemonRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(id: Int): DetailViewModel {
        val handle = SavedStateHandle(mapOf("speciesId" to id))
        return DetailViewModel(handle, repository)
    }

    //  initial Loading state

    @Test
    fun uiStateStartsAsLoading() = runTest {
        coEvery { repository.getSpeciesDetail(1) } returns
                Result.success(TestData.bulbasaurSpecies)

        val vm = viewModel(1)
        // Before coroutine runs, state should be Loading
        assertTrue(vm.uiState.value is DetailUiState.Loading)
    }

    // Success path

    @Test
    fun uiStateEmitsLoadingThenSuccessWithCorrectSpecies() = runTest {
        coEvery { repository.getSpeciesDetail(1) } returns
                Result.success(TestData.bulbasaurSpecies)

        viewModel(1).uiState.test {
            assertTrue(awaitItem() is DetailUiState.Loading)
            testDispatcher.scheduler.runCurrent()

            val success = awaitItem() as DetailUiState.Success
            assertEquals("bulbasaur", success.species.name)
            assertEquals(45, success.species.captureRate)
            assertEquals("green", success.species.colorName)
            assertEquals(2, success.species.pokemons.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiStateSuccessContainsCorrectPokemonAbilities() = runTest {
        coEvery { repository.getSpeciesDetail(1) } returns
                Result.success(TestData.bulbasaurSpecies)

        viewModel(1).uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.runCurrent()

            val success = awaitItem() as DetailUiState.Success
            val firstPokemon = success.species.pokemons.first()
            assertEquals("bulbasaur", firstPokemon.name)
            assertEquals(listOf("overgrow", "chlorophyll"), firstPokemon.abilities)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Not found path

    @Test
    fun uiStateEmitsErrorWhenRepositoryReturnsNullSpecies() = runTest {
        coEvery { repository.getSpeciesDetail(9999) } returns Result.success(null)

        viewModel(9999).uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.runCurrent()

            val error = awaitItem() as DetailUiState.Error
            assertTrue(error.message.contains("not found", ignoreCase = true))
            cancelAndIgnoreRemainingEvents()
        }
    }

    //  Error paths

    @Test
    fun uiStateEmitsErrorWithMessageOnRepositoryFailure() = runTest {
        coEvery { repository.getSpeciesDetail(4) } returns
                Result.failure(RuntimeException("Network error"))

        viewModel(4).uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.runCurrent()

            val error = awaitItem() as DetailUiState.Error
            assertEquals("Network error", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiStateErrorMessageFallsBackToUnknownErrorWhenExceptionHasNoMessage() = runTest {
        coEvery { repository.getSpeciesDetail(4) } returns
                Result.failure(RuntimeException()) // no message

        viewModel(4).uiState.test {
            awaitItem() // Loading
            testDispatcher.scheduler.runCurrent()

            val error = awaitItem() as DetailUiState.Error
            assertEquals("Unknown Error", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun uiStateErrorMessageIsNonEmptyForAnyFailure() = runTest {
        coEvery { repository.getSpeciesDetail(any()) } returns
                Result.failure(Exception("Something went wrong"))

        viewModel(1).uiState.test {
            awaitItem()
            testDispatcher.scheduler.runCurrent()

            val error = awaitItem() as DetailUiState.Error
            assertTrue(error.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Different species IDs

    @Test
    fun loadsPikachuSpeciesCorrectly() = runTest {
        coEvery { repository.getSpeciesDetail(25) } returns
                Result.success(TestData.pikachuSpecies)

        viewModel(25).uiState.test {
            awaitItem()
            testDispatcher.scheduler.runCurrent()

            val success = awaitItem() as DetailUiState.Success
            assertEquals("pikachu", success.species.name)
            assertEquals(190, success.species.captureRate)
            assertEquals("yellow", success.species.colorName)
            assertEquals(listOf("static", "lightning-rod"), success.species.pokemons.first().abilities)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun speciesWithNullCaptureRateIsHandledWithoutCrash() = runTest {
        coEvery { repository.getSpeciesDetail(999) } returns
                Result.success(TestData.nullColorSpecies)

        viewModel(999).uiState.test {
            awaitItem()
            testDispatcher.scheduler.runCurrent()

            val success = awaitItem() as DetailUiState.Success
            assertNull(success.species.captureRate)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
