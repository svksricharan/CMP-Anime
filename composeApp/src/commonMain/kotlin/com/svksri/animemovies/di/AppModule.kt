package com.svksri.animemovies.di

import com.svksri.animemovies.core.DefaultDispatcherProvider
import com.svksri.animemovies.core.DispatcherProvider
import com.svksri.animemovies.data.repository.AnimeRepositoryImpl
import com.svksri.animemovies.domain.repository.AnimeRepository
import com.svksri.animemovies.domain.usecase.GetMoviesUseCase
import com.svksri.animemovies.domain.usecase.SearchMoviesUseCase
import com.svksri.animemovies.network.AnimeApiService
import com.svksri.animemovies.network.AnimeApiServiceImpl
import com.svksri.animemovies.network.HttpClientFactory
import com.svksri.animemovies.presentation.MoviesViewModel
import com.svksri.animemovies.validation.MovieValidator
import io.ktor.client.HttpClient

object AppModule {
    private val dispatcherProvider: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    private val httpClient: HttpClient by lazy {
        HttpClientFactory.createApiClient()
    }

    val imageHttpClient: HttpClient by lazy {
        HttpClientFactory.createImageClient()
    }

    private val animeApiService: AnimeApiService by lazy {
        AnimeApiServiceImpl(httpClient)
    }

    private val movieValidator: MovieValidator by lazy {
        MovieValidator()
    }

    private val animeRepository: AnimeRepository by lazy {
        AnimeRepositoryImpl(animeApiService)
    }

    private val getMoviesUseCase: GetMoviesUseCase by lazy {
        GetMoviesUseCase(
            repository = animeRepository,
            validator = movieValidator
        )
    }

    private val searchMoviesUseCase: SearchMoviesUseCase by lazy {
        SearchMoviesUseCase(
            repository = animeRepository,
            validator = movieValidator
        )
    }

    fun createMoviesViewModel(): MoviesViewModel {
        return MoviesViewModel(
            getMoviesUseCase = getMoviesUseCase,
            searchMoviesUseCase = searchMoviesUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }
}
