package com.svksri.animemovies.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeImagesDto(
    @SerialName("jpg")
    val jpg: JpgImageDto? = null
)

@Serializable
data class JpgImageDto(
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("small_image_url")
    val smallImageUrl: String? = null,
    @SerialName("large_image_url")
    val largeImageUrl: String? = null
)

fun AnimeImagesDto?.toPosterUrl(): String? {
    val jpg = this?.jpg ?: return null
    return jpg.smallImageUrl?.takeIf { it.isNotBlank() }
        ?: jpg.imageUrl?.takeIf { it.isNotBlank() }
        ?: jpg.largeImageUrl?.takeIf { it.isNotBlank() }
}
