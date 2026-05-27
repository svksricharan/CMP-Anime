package com.svksri.animemovies.network.dto

import kotlin.test.Test
import kotlin.test.assertEquals

class AnimeImagesDtoTest {
    @Test
    fun `toPosterUrl prefers small image for list performance`() {
        val images = AnimeImagesDto(
            jpg = JpgImageDto(
                imageUrl = "https://cdn.example.com/full.jpg",
                smallImageUrl = "https://cdn.example.com/small.jpg"
            )
        )

        assertEquals("https://cdn.example.com/small.jpg", images.toPosterUrl())
    }
}
