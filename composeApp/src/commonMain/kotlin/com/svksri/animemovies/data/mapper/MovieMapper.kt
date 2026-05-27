package com.svksri.animemovies.data.mapper

import com.svksri.animemovies.domain.model.Movie
import com.svksri.animemovies.network.dto.MovieDto
import com.svksri.animemovies.network.dto.toPosterUrl

fun MovieDto.toDomain(fallbackRank: Int = 0): Movie = Movie(
    title = title?.trim().orEmpty(),
    rank = rank?.takeIf { it > 0 } ?: malId ?: fallbackRank,
    malId = malId,
    posterUrl = images.toPosterUrl(),
    synopsis = synopsis?.trim().orEmpty(),
    score = score,
    genres = genres.orEmpty().mapNotNull { genre -> genre.name?.takeIf { it.isNotBlank() } },
    rating = rating?.takeIf { it.isNotBlank() }
)
