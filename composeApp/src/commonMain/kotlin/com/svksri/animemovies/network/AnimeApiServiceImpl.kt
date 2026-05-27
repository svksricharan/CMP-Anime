package com.svksri.animemovies.network

import com.svksri.animemovies.core.Constants
import com.svksri.animemovies.network.dto.MovieResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AnimeApiServiceImpl(
    private val httpClient: HttpClient
) : AnimeApiService {
    override suspend fun getMovies(page: Int): MovieResponseDto {
        return httpClient.get(Constants.MOVIES_ENDPOINT) {
            parameter("type", "movie")
            parameter("page", page)
        }.body()
    }

    override suspend fun searchMovies(query: String, page: Int): MovieResponseDto {
        return httpClient.get(Constants.SEARCH_ENDPOINT) {
            parameter("q", query)
            parameter("type", "movie")
            parameter("page", page)
        }.body()
    }
}
