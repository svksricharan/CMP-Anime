package com.svksri.animemovies.ui.image

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import io.ktor.client.HttpClient

expect fun ImageLoader.Builder.installImageNetworkFetcher(httpClient: HttpClient): ImageLoader.Builder

fun createImageLoader(
    context: PlatformContext,
    httpClient: HttpClient
): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, percent = 0.25)
                .build()
        }
        .crossfade(false)
        .installImageNetworkFetcher(httpClient)
        .build()
}

@Composable
fun ConfigureCoilImageLoader(httpClient: HttpClient) {
    setSingletonImageLoaderFactory { context ->
        createImageLoader(context, httpClient)
    }
}
