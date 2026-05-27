package com.svksri.animemovies.domain.model

data class PagedMovies(
    val movies: List<Movie>,
    val currentPage: Int,
    val hasNextPage: Boolean
)
