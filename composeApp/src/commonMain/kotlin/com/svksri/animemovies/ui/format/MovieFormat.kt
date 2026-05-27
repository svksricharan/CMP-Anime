package com.svksri.animemovies.ui.format

import com.svksri.animemovies.domain.model.Movie

fun formatScore(score: Double): String {
    val rounded = ((score * 10).toInt() / 10.0)
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}

fun formatContentRating(rating: String): String =
    rating.substringBefore(" - ").trim().ifBlank { rating }

/** Compact subtitle for list rows: score, up to two genres, and content rating. */
fun Movie.toListSubtitle(): String? {
    val parts = buildList {
        score?.let { add("★ ${formatScore(it)}") }
        val genresLabel = genres.take(2).joinToString(" · ")
        if (genresLabel.isNotBlank()) add(genresLabel)
        rating?.let { add(formatContentRating(it)) }
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}
