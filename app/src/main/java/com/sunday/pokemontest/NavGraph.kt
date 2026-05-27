package com.sunday.pokemontest

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sunday.pokemontest.ui.home.HomeScreen
import com.sunday.pokemontest.ui.splash.SplashScreen

private object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val DETAIL = "detail/{speciesId}"
    fun detail(id: Int) = "detail/$id"
}

@Composable
fun PokemonNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }
        composable(Routes.HOME) {
            HomeScreen(onSpeciesClicked = { id ->
                navController.navigate(Routes.detail(id))
            })
        }
    }
}