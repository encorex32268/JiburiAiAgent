package com.lihan.jiburiaiagent

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.lihan.jiburiaiagent.detail.presentation.MovieDetailRoot
import com.lihan.jiburiaiagent.explore.presentation.MovieListRoot
import kotlinx.serialization.Serializable

@Serializable
object MovieListRoute

@Serializable
data class MovieDetailRoute(val filmId: String)

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = MovieListRoute
    ) {
        composable<MovieListRoute> {
            MovieListRoot(
                onNavigateToDetail = { id ->
                    navController.navigate(MovieDetailRoute(filmId = id))
                }
            )
        }
        composable<MovieDetailRoute> {
            MovieDetailRoot(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
