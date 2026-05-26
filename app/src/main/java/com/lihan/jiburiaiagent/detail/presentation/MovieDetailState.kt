package com.lihan.jiburiaiagent.detail.presentation

import com.lihan.jiburiaiagent.core.presentation.UiText
import com.lihan.jiburiaiagent.explore.domain.Movie

data class MovieCharacter(
    val name: String,
    val imageUrl: String
)

data class MovieDetailState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val isOnWatchlist: Boolean = false,
    val characters: List<MovieCharacter> = emptyList()
)
