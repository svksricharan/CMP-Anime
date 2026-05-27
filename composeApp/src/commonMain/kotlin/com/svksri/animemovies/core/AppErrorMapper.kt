package com.svksri.animemovies.core

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

const val OFFLINE_ERROR_TITLE = "You're offline"
const val OFFLINE_ERROR_MESSAGE =
    "No internet connection. Turn on Wi‑Fi or mobile data and try again."

fun Throwable.toAppError(): AppError {
    if (this is CancellationException) throw this

    return when (this) {
        is HttpRequestTimeoutException -> AppError.Timeout(message)

        is SerializationException,
        is NoTransformationFoundException -> AppError.Serialization(message)

        is ResponseException -> AppError.Network(message)

        else -> {
            val rootCause = cause
            when {
                isConnectivityFailure() -> AppError.Network(OFFLINE_ERROR_MESSAGE)
                rootCause != null && rootCause !== this -> rootCause.toAppError()
                else -> AppError.Unknown(this)
            }
        }
    }
}

private fun Throwable.isConnectivityFailure(): Boolean {
    if (this is HttpRequestTimeoutException) return true

    val typeName = this::class.simpleName.orEmpty()
    if (typeName in CONNECTIVITY_EXCEPTION_NAMES) return true

    val message = message.orEmpty().lowercase()
    if (CONNECTIVITY_MESSAGE_KEYWORDS.any { keyword -> message.contains(keyword) }) {
        return true
    }

    return cause?.isConnectivityFailure() == true
}

private val CONNECTIVITY_EXCEPTION_NAMES = setOf(
    "UnknownHostException",
    "ConnectException",
    "SocketException",
    "SocketTimeoutException",
    "ConnectTimeoutException",
    "ClientRequestException",
    "IOException",
    "NetworkException",
    "HttpException"
)

private val CONNECTIVITY_MESSAGE_KEYWORDS = listOf(
    "unable to resolve host",
    "network is unreachable",
    "not connected to internet",
    "internet connection appears to be offline",
    "connection refused",
    "connection reset",
    "failed to connect",
    "network error",
    "no network",
    "offline",
    "nsurlerror",
    "err_internet_disconnected",
    "err_network_changed",
    "err_connection_refused",
    "err_name_not_resolved",
    "timeout",
    "timed out"
)
