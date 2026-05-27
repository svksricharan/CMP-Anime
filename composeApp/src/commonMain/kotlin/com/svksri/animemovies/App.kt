package com.svksri.animemovies

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.svksri.animemovies.di.AppModule
import com.svksri.animemovies.navigation.AppNavGraph
import com.svksri.animemovies.ui.image.ConfigureCoilImageLoader
import com.svksri.animemovies.ui.theme.AnimeMoviesTheme
import com.svksri.animemovies.ui.theme.ThemeMode

@Composable
fun MoviesApp(
    appModule: AppModule = AppModule,
    onNavHostReady: suspend (NavHostController) -> Unit = {}
) {
    ConfigureCoilImageLoader(httpClient = appModule.imageHttpClient)
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.Light) }
    val navController = rememberNavController()

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    AnimeMoviesTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            AppNavGraph(
                navController = navController,
                moviesViewModelFactory = appModule.moviesViewModelFactory,
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it }
            )
        }
    }
}
