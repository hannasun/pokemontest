package com.sunday.pokemontest.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunday.pokemontest.data.PokemonSpecies
import com.sunday.pokemontest.data.repository.PokemonRepository

class PokemonPageSource(
    private val repository: PokemonRepository,
    private val query: String
) : PagingSource<Int, PokemonSpecies>() {
    override fun getRefreshKey(state: PagingState<Int, PokemonSpecies>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(PAGE_SIZE)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonSpecies> {
        val offset = params.key ?: 0
        return repository.searchSpecies(
            query,
            PAGE_SIZE, offset
        ).fold(
            onSuccess = { (species, total) ->
                LoadResult.Page(
                    data = species,
                    prevKey = if (offset == 0) null else offset - PAGE_SIZE,
                    nextKey = if (offset + PAGE_SIZE >= total) null else offset + PAGE_SIZE
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}