package com.svksri.animemovies.navigation

import com.svksri.animemovies.domain.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Type-safe navigation destinations. Add new screens here and register them in [AppNavGraph]. */
@Serializable
@SerialName("movies")
data object MoviesListRoute

@Serializable
@SerialName("movie")
data class MovieDetailRoute(
    val title: String,
    val rank: Int,
    val malId: Int? = null,
    val posterUrl: String? = null,
    val synopsis: String = "",
    val score: Double? = null,
    val genres: List<String> = emptyList(),
    val rating: String? = null
)

fun Movie.toDetailRoute(): MovieDetailRoute = MovieDetailRoute(
    title = title,
    rank = rank,
    malId = malId,
    posterUrl = posterUrl,
    synopsis = synopsis,
    score = score,
    genres = genres,
    rating = rating
)

fun MovieDetailRoute.toMovie(): Movie = Movie(
    title = title,
    rank = rank,
    malId = malId,
    posterUrl = posterUrl,
    synopsis = synopsis,
    score = score,
    genres = genres,
    rating = rating
)
