package com.sunday.pokemontest.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunday.pokemontest.TestData
import com.sunday.pokemontest.domain.model.PokemonSpecies
import com.sunday.pokemontest.data.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class PokemonPageSourceTest {
    private lateinit var repository: PokemonRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    private fun source(query: String = "bulba") = PokemonPageSource(repository, query)

    @Test
    fun firstPageNullPrevKeyAndNonNullNextKeyWhenMorePages() = runTest {
        ///**/total = 13, page size = 10 → two pages
        coEvery { repository.searchSpecies("bulba", 10, 0) } returns
                Result.success(TestData.fullPage to 13)

        val result = source().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertNull("prevKey should be null on first page", page.prevKey)
        assertEquals("nextKey should point to offset 10", 10, page.nextKey)
        assertEquals(10, page.data.size)
    }

    @Test
    fun lastPageNonNullPreKeyAndNullNextKey() = runTest {
        // offset=10, total=13 → offset+pageSize(20) >= total(13) → nextKey = null
        coEvery { repository.searchSpecies("bulba", 10, 10) } returns
                Result.success(TestData.partialPage to 13)

        val result = source().load(
            PagingSource.LoadParams.Append(
                key = 10,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals("prevKey should be 0 (10 - 10)", 0, page.prevKey)
        assertNull("nextKey should be null on last page", page.nextKey)
        assertEquals(3, page.data.size)
    }

    @Test
    fun emptyResultsWithNullKeys() = runTest {
        coEvery { repository.searchSpecies("xyz", 10, 0) } returns
                Result.success(emptyList<PokemonSpecies>() to 0)

        val result = source("xyz").load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun networkErrorLoadError() = runTest {
        val error = RuntimeException("Network timeout")
        coEvery { repository.searchSpecies("bulba", 10, 0) } returns Result.failure(error)

        val result = source().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals("Network timeout", (result as PagingSource.LoadResult.Error).throwable.message)
    }

    @Test
    fun getRefreshKeyReturnNullWhenAnchorPositionNull() {
        val state = mockk<PagingState<Int, PokemonSpecies>> {
            every { anchorPosition } returns null
        }
        assertNull(source().getRefreshKey(state))
    }
}