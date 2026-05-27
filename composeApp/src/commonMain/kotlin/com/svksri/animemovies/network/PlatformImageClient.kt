package com.svksri.animemovies.network

import io.ktor.client.HttpClient

internal expect fun buildImageClient(): HttpClient
