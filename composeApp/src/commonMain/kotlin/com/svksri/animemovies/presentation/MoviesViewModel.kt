package com.svksri.animemovies.presentation

import com.svksri.animemovies.core.AppError
import com.svksri.animemovies.core.AppResult
import com.svksri.animemovies.core.Constants
import com.svksri.animemovies.core.DispatcherProvider
import com.svksri.animemovies.core.OFFLINE_ERROR_MESSAGE
import com.svksri.animemovies.core.OFFLINE_ERROR_TITLE
import com.svksri.animemovies.core.toAppError
import com.svksri.animemovies.domain.model.Movie
import com.svksri.animemovies.domain.model.PagedMovies
import com.svksri.animemovies.domain.usecase.GetMoviesUseCase
import com.svksri.animemovies.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val dispatcherProvider: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)
    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    private val _searchQuery = MutableStateFlow("")
    private var loadJob: Job? = null
    private var searchDebounceJob: Job? = null
    private var isLoadingMore = false
    private var isFetchInFlight = false
    private var isSearchActive = false
    private var activeSearchQuery = ""
    private var retryAttemptsUsed = 0

    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun loadMovies() {
        searchDebounceJob?.cancel()
        retryAttemptsUsed = 0
        isSearchActive = false
        activeSearchQuery = ""
        fetchFirstPage(page = 1, isSearch = false, query = "")
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()

        if (query.isBlank()) {
            loadMovies()
            return
        }

        if (query.length < Constants.MIN_SEARCH_LENGTH) {
            return
        }

        searchDebounceJob = scope.launch {
            delay(Constants.SEARCH_DEBOUNCE_MS)
            searchMovies(query)
        }
    }

    fun clearSearch() {
        if (_searchQuery.value.isEmpty()) return
        _searchQuery.value = ""
        loadMovies()
    }

    fun loadNextPage() {
        val currentState = _uiState.value as? MoviesUiState.Success ?: return
        if (!currentState.hasNextPage || isLoadingMore || isFetchInFlight) return

        isLoadingMore = true
        _uiState.value = currentState.copy(isLoadingMore = true, loadMoreError = null)

        val nextPage = currentState.currentPage + 1
        loadJob?.cancel()
        loadJob = scope.launch {
            isFetchInFlight = true
            try {
                val result = executeFetch(isSearchActive, activeSearchQuery, nextPage)
                when (result) {
                    is AppResult.Success -> {
                        val mergedMovies = mergeMovies(currentState.movies, result.data.movies)
                        retryAttemptsUsed = 0
                        _uiState.value = MoviesUiState.Success(
                            movies = mergedMovies,
                            currentPage = result.data.currentPage,
                            hasNextPage = result.data.hasNextPage,
                            isSearchMode = isSearchActive,
                            searchQuery = activeSearchQuery,
                            isLoadingMore = false
                        )
                    }

                    is AppResult.Error -> {
                        _uiState.value = currentState.copy(
                            isLoadingMore = false,
                            loadMoreError = result.error.toUserMessage()
                        )
                    }

                    AppResult.Loading -> Unit
                }
            } catch (_: CancellationException) {
                // Keep current list state.
            } finally {
                isLoadingMore = false
                isFetchInFlight = false
            }
        }
    }

    fun retry() {
        searchDebounceJob?.cancel()

        if (isFetchInFlight) return

        if (retryAttemptsUsed >= Constants.MAX_RETRY_ATTEMPTS) {
            _uiState.value = MoviesUiState.Error(
                title = OFFLINE_ERROR_TITLE,
                message = "Maximum retry attempts reached. Check your connection and try again later.",
                canRetry = false
            )
            return
        }

        retryAttemptsUsed++

        if (isSearchActive) {
            fetchFirstPage(page = 1, isSearch = true, query = activeSearchQuery)
        } else {
            fetchFirstPage(page = 1, isSearch = false, query = "")
        }
    }

    fun retryLoadMore() {
        loadNextPage()
    }

    fun clear() {
        loadJob?.cancel()
        searchDebounceJob?.cancel()
        scope.cancel()
    }

    private fun searchMovies(query: String) {
        retryAttemptsUsed = 0
        isSearchActive = true
        activeSearchQuery = query.trim()
        fetchFirstPage(page = 1, isSearch = true, query = activeSearchQuery)
    }

    private fun fetchFirstPage(page: Int, isSearch: Boolean, query: String) {
        searchDebounceJob?.cancel()

        if (isFetchInFlight) return

        loadJob?.cancel()
        loadJob = scope.launch {
            isFetchInFlight = true
            try {
                _uiState.value = MoviesUiState.Loading

                val result = try {
                    executeFetch(isSearch, query, page)
                } catch (e: CancellationException) {
                    return@launch
                } catch (e: Throwable) {
                    AppResult.Error(e.toAppError())
                }

                when (result) {
                    is AppResult.Success -> {
                        retryAttemptsUsed = 0
                        applyFirstPage(
                            page = result.data,
                            isSearchMode = isSearch,
                            searchQuery = query
                        )
                    }

                    is AppResult.Error -> applyError(result.error, isSearch)
                    AppResult.Loading -> _uiState.value = MoviesUiState.Loading
                }
            } finally {
                isFetchInFlight = false
            }
        }
    }

    private suspend fun executeFetch(
        isSearch: Boolean,
        query: String,
        page: Int
    ): AppResult<PagedMovies> {
        return withContext(dispatcherProvider.io) {
            if (isSearch) {
                searchMoviesUseCase(query, page)
            } else {
                getMoviesUseCase(page = page)
            }
        }
    }

    private fun applyFirstPage(page: PagedMovies, isSearchMode: Boolean, searchQuery: String) {
        _uiState.value = if (page.movies.isEmpty()) {
            if (isSearchMode) {
                MoviesUiState.Empty(
                    title = "No results",
                    message = "No anime movies matched \"$searchQuery\". Try another title."
                )
            } else {
                MoviesUiState.Empty()
            }
        } else {
            MoviesUiState.Success(
                movies = page.movies,
                currentPage = page.currentPage,
                hasNextPage = page.hasNextPage,
                isSearchMode = isSearchMode,
                searchQuery = searchQuery
            )
        }
    }

    private fun applyError(error: AppError, isSearch: Boolean) {
        _uiState.value = if (error == AppError.EmptyResponse) {
            if (isSearch) {
                MoviesUiState.Empty(
                    title = "No results",
                    message = "No anime movies matched your search."
                )
            } else {
                MoviesUiState.Empty()
            }
        } else {
            MoviesUiState.Error(
                title = error.toErrorTitle(),
                message = error.toUserMessage(),
                canRetry = retryAttemptsUsed < Constants.MAX_RETRY_ATTEMPTS
            )
        }
    }

    private fun mergeMovies(existing: List<Movie>, newPage: List<Movie>): List<Movie> {
        return (existing + newPage)
            .distinctBy { movie -> movie.malId ?: "${movie.rank}-${movie.title}" }
            .let { movies ->
                if (isSearchActive) {
                    movies.sortedBy(Movie::title)
                } else {
                    movies.sortedBy(Movie::rank)
                }
            }
    }
}

private fun AppError.toErrorTitle(): String = when (this) {
    is AppError.Network,
    is AppError.Timeout -> OFFLINE_ERROR_TITLE

    else -> "Something went wrong"
}

private fun AppError.toUserMessage(): String = when (this) {
    is AppError.Network -> message ?: OFFLINE_ERROR_MESSAGE
    is AppError.Serialization -> message ?: "Unable to read the server response."
    is AppError.Timeout -> OFFLINE_ERROR_MESSAGE
    AppError.EmptyResponse -> "No movies found."
    is AppError.Unknown -> OFFLINE_ERROR_MESSAGE
}
