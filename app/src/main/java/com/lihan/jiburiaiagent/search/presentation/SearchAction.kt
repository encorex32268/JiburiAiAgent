package com.lihan.jiburiaiagent.search.presentation

sealed interface SearchAction {
    data class OnQueryChanged(val query: String) : SearchAction
    data object OnClearQuery : SearchAction
    data class OnMovieClicked(val movieId: String) : SearchAction
}
