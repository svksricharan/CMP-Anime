package com.svksri.animemovies.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    @SerialName("mal_id")
    val malId: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("rank")
    val rank: Int? = null,
    @SerialName("images")
    val images: AnimeImagesDto? = null,
    @SerialName("synopsis")
    val synopsis: String? = null,
    @SerialName("score")
    val score: Double? = null,
    @SerialName("genres")
    val genres: List<GenreDto>? = null,
    @SerialName("rating")
    val rating: String? = null
)
