package com.svksri.animemovies.presentation

import com.svksri.animemovies.domain.model.Movie

sealed interface MoviesUiState {
    data object Loading : MoviesUiState

    data class Success(
        val movies: List<Movie>,
        val currentPage: Int,
        val hasNextPage: Boolean,
        val isSearchMode: Boolean = false,
        val searchQuery: String = "",
        val isLoadingMore: Boolean = false,
        val loadMoreError: String? = null
    ) : MoviesUiState

    data class Error(
        val title: String = "Something went wrong",
        val message: String,
        val canRetry: Boolean = true
    ) : MoviesUiState

    data class Empty(
        val title: String = "No movies found",
        val message: String = "Try a different search or pull to refresh the top list."
    ) : MoviesUiState
}
