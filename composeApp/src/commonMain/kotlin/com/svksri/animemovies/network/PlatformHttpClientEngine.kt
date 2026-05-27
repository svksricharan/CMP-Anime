package com.svksri.animemovies.network

import io.ktor.client.engine.HttpClientEngineFactory

expect fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*>
