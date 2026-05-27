package com.svksri.animemovies.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.size.Size

private val DefaultPosterSize = DpSize(width = 72.dp, height = 108.dp)

@Composable
fun MoviePoster(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    width: Dp? = DefaultPosterSize.width,
    height: Dp = DefaultPosterSize.height
) {
    val posterModifier = modifier.then(
        if (width != null) {
            Modifier.width(width).height(height)
        } else {
            Modifier.fillMaxWidth().height(height)
        }
    )

    if (imageUrl.isNullOrBlank()) {
        DefaultPosterPlaceholder(modifier = posterModifier)
        return
    }

    val context = LocalPlatformContext.current
    val density = LocalDensity.current
    val request = remember(imageUrl, context, density, width, height) {
        val widthPx = width?.let { with(density) { it.roundToPx() } }
            ?: with(density) { DefaultPosterSize.width.roundToPx() * 4 }
        val heightPx = with(density) { height.roundToPx() }

        ImageRequest.Builder(context)
            .data(imageUrl)
            .memoryCacheKey(imageUrl)
            .diskCacheKey(imageUrl)
            .size(Size(width = widthPx, height = heightPx))
            .build()
    }

    SubcomposeAsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = posterModifier,
        contentScale = ContentScale.Crop,
        loading = {
            DefaultPosterPlaceholder(modifier = Modifier.matchParentSize())
        },
        error = {
            DefaultPosterPlaceholder(modifier = Modifier.matchParentSize())
        }
    )
}
