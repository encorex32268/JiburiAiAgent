package com.lihan.jiburiaiagent.search.presentation

import com.lihan.jiburiaiagent.explore.domain.Movie

data class SearchState(
    val query: String = "",
    val results: List<Movie> = emptyList()
)
