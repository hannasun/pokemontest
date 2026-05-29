package com.sunday.pokemontest.common.extension

import com.sunday.pokemontest.common.constant.ApiConstants.POKEMON_ARTWORK_BASE_URL

fun Int.toPokemonImageUrl(): String = "${POKEMON_ARTWORK_BASE_URL}$this.png"