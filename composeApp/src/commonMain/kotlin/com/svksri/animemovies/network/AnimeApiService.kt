package com.svksri.animemovies.network

import com.svksri.animemovies.network.dto.MovieResponseDto

interface AnimeApiService {
    suspend fun getMovies(page: Int = 1): MovieResponseDto

    suspend fun searchMovies(query: String, page: Int = 1): MovieResponseDto
}
