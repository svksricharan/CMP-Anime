package com.svksri.animemovies.domain.repository

import com.svksri.animemovies.core.AppResult
import com.svksri.animemovies.domain.model.PagedMovies

interface AnimeRepository {
    suspend fun getMovies(page: Int = 1): AppResult<PagedMovies>

    suspend fun searchMovies(query: String, page: Int = 1): AppResult<PagedMovies>
}
