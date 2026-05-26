package com.lihan.jiburiaiagent.explore.presentation

sealed interface MovieListAction {
    data class OnMovieClick(val movieId: String) : MovieListAction
    data class OnCategorySelect(val category: String) : MovieListAction
    data class OnTabSelect(val tab: ExploreTab) : MovieListAction
    data object OnRefresh : MovieListAction
}
