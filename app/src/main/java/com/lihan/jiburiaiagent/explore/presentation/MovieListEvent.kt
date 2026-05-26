package com.lihan.jiburiaiagent.explore.presentation

import com.lihan.jiburiaiagent.core.presentation.UiText

sealed interface MovieListEvent {
    data class NavigateToDetail(val movieId: String) : MovieListEvent
    data class ShowSnackbar(val message: UiText) : MovieListEvent
}
