package com.svksri.animemovies.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationDto(
    @SerialName("current_page")
    val currentPage: Int? = null,
    @SerialName("last_visible_page")
    val lastVisiblePage: Int? = null,
    @SerialName("has_next_page")
    val hasNextPage: Boolean? = null
)
