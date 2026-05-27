package com.svksri.animemovies.domain.model

data class Movie(
    val title: String,
    val rank: Int,
    val malId: Int? = null,
    val posterUrl: String? = null,
    val synopsis: String = "",
    val score: Double? = null,
    val genres: List<String> = emptyList(),
    val rating: String? = null
)
