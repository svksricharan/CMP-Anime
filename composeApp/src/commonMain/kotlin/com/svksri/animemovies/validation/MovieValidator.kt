package com.svksri.animemovies.validation

import com.svksri.animemovies.domain.model.Movie

class MovieValidator {
    fun validate(movie: Movie): ValidationResult {
        val errors = buildList {
            if (movie.title.isBlank()) {
                add("Movie title must not be blank.")
            }
            if (movie.rank <= 0) {
                add("Movie rank must be greater than zero.")
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
