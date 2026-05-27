package com.svksri.animemovies.domain.usecase

import com.svksri.animemovies.core.AppResult
import com.svksri.animemovies.domain.model.Movie
import com.svksri.animemovies.domain.model.PagedMovies
import com.svksri.animemovies.domain.repository.AnimeRepository
import com.svksri.animemovies.validation.MovieValidator
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMoviesUseCaseTest {
    @Test
    fun `invoke filters invalid movies and sorts by ascending rank`() = runTest {
        val repository = object : AnimeRepository {
            override suspend fun searchMovies(query: String, page: Int): AppResult<PagedMovies> {
                error("Not used")
            }

            override suspend fun getMovies(page: Int): AppResult<PagedMovies> {
                return AppResult.Success(
                    PagedMovies(
                        movies = listOf(
                            Movie(title = "Akira", rank = 3),
                            Movie(title = "", rank = 2),
                            Movie(title = "Spirited Away", rank = 1)
                        ),
                        currentPage = page,
                        hasNextPage = page < 2
                    )
                )
            }
        }

        val useCase = GetMoviesUseCase(
            repository = repository,
            validator = MovieValidator()
        )

        val result = useCase(page = 1)

        assertTrue(result is AppResult.Success)
        assertEquals(
            listOf(
                Movie(title = "Spirited Away", rank = 1),
                Movie(title = "Akira", rank = 3)
            ),
            result.data.movies
        )
        assertTrue(result.data.hasNextPage)
        assertEquals(1, result.data.currentPage)
    }
}
