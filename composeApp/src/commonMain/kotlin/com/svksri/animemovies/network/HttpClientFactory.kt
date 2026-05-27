package com.svksri.animemovies.network

import com.svksri.animemovies.core.Constants
import com.svksri.animemovies.core.logging.logApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun createApiClient(): HttpClient {
        return HttpClient(platformHttpClientEngineFactory()) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        logApi(message)
                    }
                }
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = Constants.REQUEST_TIMEOUT_MS
                connectTimeoutMillis = Constants.CONNECT_TIMEOUT_MS
                socketTimeoutMillis = Constants.REQUEST_TIMEOUT_MS
            }
            defaultRequest {
                url(Constants.BASE_URL)
                contentType(ContentType.Application.Json)
                headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }

    /** Plain client for image CDNs — no JSON defaults or API base URL. */
    fun createImageClient(): HttpClient = buildImageClient()

    fun create(): HttpClient = createApiClient()
}
