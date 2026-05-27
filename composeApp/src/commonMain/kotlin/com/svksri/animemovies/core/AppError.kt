package com.svksri.animemovies.core

sealed interface AppError {
    data class Network(val message: String? = null) : AppError

    data class Serialization(val message: String? = null) : AppError

    data class Timeout(val message: String? = null) : AppError

    data object EmptyResponse : AppError

    data class Unknown(val cause: Throwable? = null) : AppError
}
