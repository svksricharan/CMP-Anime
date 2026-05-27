package com.svksri.animemovies.ui.image

import coil3.ImageLoader
import coil3.network.ConnectivityChecker
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.ktor.client.HttpClient

actual fun ImageLoader.Builder.installImageNetworkFetcher(
    httpClient: HttpClient
): ImageLoader.Builder {
    val sharedClient = httpClient
    return components {
        add(
            KtorNetworkFetcherFactory(
                httpClient = { sharedClient },
                connectivityChecker = { ConnectivityChecker.ONLINE }
            )
        )
    }
}
