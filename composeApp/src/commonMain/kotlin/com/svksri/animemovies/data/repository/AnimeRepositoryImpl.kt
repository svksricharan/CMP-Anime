package com.svksri.animemovies.data.repository

import com.svksri.animemovies.core.AppError
import com.svksri.animemovies.core.AppResult
import com.svksri.animemovies.core.toAppError
import com.svksri.animemovies.data.mapper.toPagedMovies
import com.svksri.animemovies.domain.model.PagedMovies
import com.svksri.animemovies.domain.repository.AnimeRepository
import com.svksri.animemovies.network.AnimeApiService

class AnimeRepositoryImpl(
    private val apiService: AnimeApiService
) : AnimeRepository {
    override suspend fun getMovies(page: Int): AppResult<PagedMovies> {
        return try {
            val pageResult = apiService.getMovies(page).toPagedMovies()

            if (pageResult.movies.isEmpty()) {
                AppResult.Error(AppError.EmptyResponse)
            } else {
                AppResult.Success(pageResult)
            }
        } catch (throwable: Throwable) {
            AppResult.Error(throwable.toAppError())
        }
    }

    override suspend fun searchMovies(query: String, page: Int): AppResult<PagedMovies> {
        return try {
            val pageResult = apiService.searchMovies(query, page).toPagedMovies()

            if (pageResult.movies.isEmpty()) {
                AppResult.Error(AppError.EmptyResponse)
            } else {
                AppResult.Success(pageResult)
            }
        } catch (throwable: Throwable) {
            AppResult.Error(throwable.toAppError())
        }
    }
}
