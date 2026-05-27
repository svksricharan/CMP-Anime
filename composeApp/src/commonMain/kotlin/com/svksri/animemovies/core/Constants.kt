package com.svksri.animemovies.core

object Constants {
    const val BASE_URL = "https://api.jikan.moe"
    const val MOVIES_ENDPOINT = "/v4/top/anime"
    const val SEARCH_ENDPOINT = "/v4/anime"
    const val CONNECT_TIMEOUT_MS = 8_000L
    const val REQUEST_TIMEOUT_MS = 15_000L
    const val SEARCH_DEBOUNCE_MS = 400L
    const val MIN_SEARCH_LENGTH = 2
    const val MAX_RETRY_ATTEMPTS = 2
}
