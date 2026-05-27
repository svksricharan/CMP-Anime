package com.svksri.animemovies.ui.image

import coil3.ImageLoader
import coil3.network.ConnectivityChecker
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import io.ktor.client.HttpClient

actual fun ImageLoader.Builder.installImageNetworkFetcher(
    httpClient: HttpClient
): ImageLoader.Builder {
    return components {
        add(
            OkHttpNetworkFetcherFactory(
                connectivityChecker = { ConnectivityChecker.ONLINE }
            )
        )
    }
}
