package com.svksri.animemovies.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppErrorMapperTest {
    @Test
    fun `maps host resolution failures to offline network error`() {
        val error = Exception("Unable to resolve host api.jikan.moe").toAppError()

        assertTrue(error is AppError.Network)
        assertEquals(OFFLINE_ERROR_MESSAGE, error.message)
    }

    @Test
    fun `maps nested connectivity cause to network error`() {
        val error = Exception(
            "Request failed",
            Exception("Network is unreachable")
        ).toAppError()

        assertTrue(error is AppError.Network)
    }
}
