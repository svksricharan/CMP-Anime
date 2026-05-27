package com.svksri.animemovies.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.svksri.animemovies.presentation.MoviesUiState
import com.svksri.animemovies.presentation.MoviesViewModel
import com.svksri.animemovies.ui.MovieDetailScreen
import com.svksri.animemovies.ui.MoviesScreen
import com.svksri.animemovies.ui.theme.ThemeMode

@Composable
fun AppNavGraph(
    navController: NavHostController,
    moviesViewModelFactory: ViewModelProvider.Factory,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MoviesListRoute,
        modifier = modifier.fillMaxSize()
    ) {
        composable<MoviesListRoute> {
            val viewModel: MoviesViewModel = viewModel(factory = moviesViewModelFactory)
            val uiState by viewModel.uiState.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()

            LaunchedEffect(Unit) {
                if (viewModel.uiState.value is MoviesUiState.Loading) {
                    viewModel.loadMovies()
                }
            }

            MoviesScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onClearSearch = viewModel::clearSearch,
                onRetry = viewModel::retry,
                onLoadMore = viewModel::loadNextPage,
                onRetryLoadMore = viewModel::retryLoadMore,
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                onMovieClick = { movie ->
                    navController.navigate(movie.toDetailRoute())
                }
            )
        }

        composable<MovieDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MovieDetailRoute>()
            MovieDetailScreen(
                movie = route.toMovie(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
