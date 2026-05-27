package com.svksri.animemovies.validation

sealed interface ValidationResult {
    data object Valid : ValidationResult

    data class Invalid(val reasons: List<String>) : ValidationResult
}
