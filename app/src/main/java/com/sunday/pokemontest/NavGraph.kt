package com.sunday.pokemontest

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sunday.pokemontest.ui.detail.DetailScreen
import com.sunday.pokemontest.ui.home.HomeScreen

private object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{speciesId}"
    fun detail(id: Int) = "detail/$id"
}

@Composable
fun PokemonNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onSpeciesClicked = { id ->
                navController.navigate(Routes.detail(id))
            })
        }
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("speciesId") {
                type = NavType.IntType
            })
        ) {
            DetailScreen {
                navController.popBackStack()
            }
        }
    }
}