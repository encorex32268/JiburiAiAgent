package com.lihan.jiburiaiagent.detail.presentation

sealed interface MovieDetailAction {
    data object OnBackClick : MovieDetailAction
    data object OnToggleWatchlist : MovieDetailAction
}
