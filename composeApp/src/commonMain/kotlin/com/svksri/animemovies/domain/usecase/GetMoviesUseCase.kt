package com.svksri.animemovies.domain.usecase

import com.svksri.animemovies.core.AppResult
import com.svksri.animemovies.domain.model.Movie
import com.svksri.animemovies.domain.model.PagedMovies
import com.svksri.animemovies.domain.repository.AnimeRepository
import com.svksri.animemovies.validation.MovieValidator
import com.svksri.animemovies.validation.ValidationResult

class GetMoviesUseCase(
    private val repository: AnimeRepository,
    private val validator: MovieValidator
) {
    suspend operator fun invoke(page: Int = 1): AppResult<PagedMovies> {
        return when (val result = repository.getMovies(page)) {
            is AppResult.Success -> {
                val validMovies = result.data.movies
                    .filter { movie -> validator.validate(movie) is ValidationResult.Valid }
                    .sortedBy(Movie::rank)

                AppResult.Success(
                    PagedMovies(
                        movies = validMovies,
                        currentPage = result.data.currentPage,
                        hasNextPage = result.data.hasNextPage
                    )
                )
            }

            is AppResult.Error -> result
            AppResult.Loading -> AppResult.Loading
        }
    }
}
