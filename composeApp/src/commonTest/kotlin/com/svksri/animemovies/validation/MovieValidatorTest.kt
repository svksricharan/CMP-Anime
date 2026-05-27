package com.svksri.animemovies.validation

import com.svksri.animemovies.domain.model.Movie
import kotlin.test.Test
import kotlin.test.assertIs

class MovieValidatorTest {
    private val validator = MovieValidator()

    @Test
    fun `validate returns valid for a complete movie`() {
        val result = validator.validate(Movie(title = "Spirited Away", rank = 1))

        assertIs<ValidationResult.Valid>(result)
    }

    @Test
    fun `validate returns invalid when title is blank`() {
        val result = validator.validate(Movie(title = " ", rank = 1))

        assertIs<ValidationResult.Invalid>(result)
    }

    @Test
    fun `validate returns invalid when rank is not positive`() {
        val result = validator.validate(Movie(title = "Akira", rank = 0))

        assertIs<ValidationResult.Invalid>(result)
    }
}
