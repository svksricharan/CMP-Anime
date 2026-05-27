package com.svksri.animemovies.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.svksri.animemovies.core.Constants
import com.svksri.animemovies.domain.model.Movie
import com.svksri.animemovies.presentation.MoviesUiState
import com.svksri.animemovies.ui.components.EmptyScreen
import com.svksri.animemovies.ui.components.ErrorScreen
import com.svksri.animemovies.ui.components.MovieCard
import com.svksri.animemovies.ui.theme.ThemeMode
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MoviesScreen(
    uiState: MoviesUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onRetryLoadMore: () -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MoviesTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onClearSearch = onClearSearch,
                themeMode = themeMode,
                onThemeModeChange = onThemeModeChange
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    MoviesUiState.Loading -> LoadingContent()
                    is MoviesUiState.Empty -> EmptyScreen(
                        title = uiState.title,
                        message = uiState.message
                    )
                    is MoviesUiState.Error -> ErrorScreen(
                        title = uiState.title,
                        message = uiState.message,
                        onRetry = onRetry,
                        canRetry = uiState.canRetry
                    )
                    is MoviesUiState.Success -> MoviesContent(
                        movies = uiState.movies,
                        currentPage = uiState.currentPage,
                        hasNextPage = uiState.hasNextPage,
                        isSearchMode = uiState.isSearchMode,
                        searchQuery = uiState.searchQuery,
                        isLoadingMore = uiState.isLoadingMore,
                        loadMoreError = uiState.loadMoreError,
                        onLoadMore = onLoadMore,
                        onRetryLoadMore = onRetryLoadMore,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MoviesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Top Anime Movies",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Powered by the Jikan API",
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = themeMode == ThemeMode.Light,
                    onClick = { onThemeModeChange(ThemeMode.Light) },
                    label = { Text("☀") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                FilterChip(
                    selected = themeMode == ThemeMode.Dark,
                    onClick = { onThemeModeChange(ThemeMode.Dark) },
                    label = { Text("🌙") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Search anime movies…") },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(onClick = onClearSearch) {
                        Text("Clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        if (searchQuery.isNotEmpty() && searchQuery.length < Constants.MIN_SEARCH_LENGTH) {
            Text(
                text = "Type at least ${Constants.MIN_SEARCH_LENGTH} characters to search",
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun MoviesContent(
    movies: List<Movie>,
    currentPage: Int,
    hasNextPage: Boolean,
    isSearchMode: Boolean,
    searchQuery: String,
    isLoadingMore: Boolean,
    loadMoreError: String?,
    onLoadMore: () -> Unit,
    onRetryLoadMore: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, hasNextPage, isLoadingMore) {
        if (!hasNextPage || isLoadingMore) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItems - 3 && totalItems > 0
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "list-header") {
            Text(
                text = when {
                    isSearchMode -> "Results for \"$searchQuery\" · Page $currentPage"
                    else -> "Page $currentPage"
                },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(
            items = movies,
            key = { movie -> movie.malId?.toString() ?: "${movie.rank}-${movie.title}" }
        ) { movie ->
            MovieCard(
                movie = movie,
                showRankLabel = !isSearchMode,
                onClick = { onMovieClick(movie) }
            )
        }

        if (isLoadingMore) {
            item(key = "loading-more") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        loadMoreError?.let { errorMessage ->
            item(key = "load-more-error") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onRetryLoadMore) {
                        Text("Retry loading more")
                    }
                }
            }
        }

        if (!hasNextPage && movies.isNotEmpty()) {
            item(key = "end-of-list") {
                Text(
                    text = "You have reached the end of the list",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
