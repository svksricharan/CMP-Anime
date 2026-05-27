package com.svksri.animemovies.network

import com.svksri.animemovies.core.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

internal actual fun buildImageClient(): HttpClient {
    return HttpClient(platformHttpClientEngineFactory()) {
        install(HttpTimeout) {
            requestTimeoutMillis = Constants.REQUEST_TIMEOUT_MS
            connectTimeoutMillis = Constants.CONNECT_TIMEOUT_MS
            socketTimeoutMillis = Constants.REQUEST_TIMEOUT_MS
        }
    }
}
