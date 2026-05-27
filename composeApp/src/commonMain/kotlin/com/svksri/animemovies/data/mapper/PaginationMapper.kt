package com.svksri.animemovies.data.mapper

import com.svksri.animemovies.domain.model.PagedMovies
import com.svksri.animemovies.network.dto.MovieResponseDto

fun MovieResponseDto.toPagedMovies(): PagedMovies {
    val pagination = pagination
    return PagedMovies(
        movies = data.mapIndexed { index, dto -> dto.toDomain(fallbackRank = index + 1) },
        currentPage = pagination?.currentPage ?: 1,
        hasNextPage = pagination?.hasNextPage ?: false
    )
}
