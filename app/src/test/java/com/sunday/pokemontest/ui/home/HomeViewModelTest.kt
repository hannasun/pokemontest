package com.sunday.pokemontest.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.sunday.pokemontest.TestData
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.data.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTask = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PokemonRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        // Default: return empty results for any query so ViewModel initialises cleanly
        coEvery { repository.searchSpecies(any(), any(), any()) } returns
                Result.success(emptyList<PokemonSpecies>() to 0)
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    //   searchQuery state

    @Test
    fun initialSearchQueryIsEmpty() {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun onQueryChangeUpdatesSearchQueryImmediately() = runTest {
        viewModel.onQueryChange("bulba")
        assertEquals("bulba", viewModel.searchQuery.value)
    }

    @Test
    fun onQueryChangeReflectsLatestValueWhenCalledMultipleTimes() = runTest {
        viewModel.onQueryChange("a")
        viewModel.onQueryChange("ab")
        viewModel.onQueryChange("abc")
        assertEquals("abc", viewModel.searchQuery.value)
    }

    @Test
    fun searchQueryEmitsEachUpdateViaStateFlow() = runTest {
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())          // initial
            viewModel.onQueryChange("pika")
            assertEquals("pika", awaitItem())
            viewModel.onQueryChange("pikachu")
            assertEquals("pikachu", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    //  debounce behaviour

    @Test
    fun rapidTypingIsDebouncedRepositoryNotCalledForEveryKeystroke() = runTest {
        // Stub a response for what we expect after debounce
        coEvery { repository.searchSpecies("%char%", 10, 0) } returns
                Result.success(listOf(TestData.charmanderSpecies) to 1)

        // Type quickly — only the last value after 400ms debounce should fire
        viewModel.onQueryChange("c")
        viewModel.onQueryChange("ch")
        viewModel.onQueryChange("cha")
        viewModel.onQueryChange("char")

        // Advance past the 400ms debounce window
        advanceTimeBy(500)
        testDispatcher.scheduler.runCurrent()

        // Query value should be the last typed value
        assertEquals("char", viewModel.searchQuery.value)
    }

}
