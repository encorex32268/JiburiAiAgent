package com.lihan.jiburiaiagent.explore.presentation

import com.lihan.jiburiaiagent.core.presentation.UiText
import com.lihan.jiburiaiagent.explore.domain.Movie

enum class ExploreTab {
    HOME, SEARCH, WATCHLIST
}

data class MovieListState(
    val movies: List<Movie> = emptyList(),
    val allMovies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val categories: List<String> = listOf("All", "Fantasy", "Drama", "Adventure", "Family"),
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val featuredMovie: Movie? = null,
    val comingSoonMovie: Movie? = null,
    val activeTab: ExploreTab = ExploreTab.HOME
)
