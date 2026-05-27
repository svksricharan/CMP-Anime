package com.svksri.animemovies.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*> = Android
