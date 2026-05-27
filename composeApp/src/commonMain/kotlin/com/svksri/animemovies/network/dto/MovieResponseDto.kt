package com.svksri.animemovies.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName("data")
    val data: List<MovieDto> = emptyList(),
    @SerialName("pagination")
    val pagination: PaginationDto? = null
)
