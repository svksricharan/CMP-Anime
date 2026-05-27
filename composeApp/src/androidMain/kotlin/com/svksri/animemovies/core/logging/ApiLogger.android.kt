package com.svksri.animemovies.core.logging

import android.util.Log

private const val TAG = "AnimeMovies-API"

internal actual fun logApi(message: String) {
    Log.d(TAG, message)
}
