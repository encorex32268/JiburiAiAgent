package com.lihan.jiburiaiagent.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.jiburiaiagent.core.domain.Result
import com.lihan.jiburiaiagent.explore.domain.Movie
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private var allMovies: List<Movie> = emptyList()
    private var searchJob: Job? = null

    init {
        observeMovies()
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChanged -> {
                _state.update { it.copy(query = action.query) }
                debounceSearch(action.query)
            }
            SearchAction.OnClearQuery -> {
                searchJob?.cancel()
                _state.update { it.copy(query = "", results = allMovies) }
            }
            is SearchAction.OnMovieClicked -> Unit
        }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            movieRepository.getMoviesFlow().collect { result ->
                when (result) {
                    is Result.Success -> {
                        allMovies = result.data
                        // Only update results if not currently searching
                        if (_state.value.query.isBlank()) {
                            _state.update { it.copy(results = allMovies) }
                        } else {
                            _state.update { it.copy(results = filterMovies(allMovies, _state.value.query)) }
                        }
                    }
                    is Result.Error -> Unit
                }
            }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val filtered = filterMovies(allMovies, query)
            _state.update { it.copy(results = filtered) }
        }
    }

    private fun filterMovies(movies: List<Movie>, query: String): List<Movie> {
        if (query.isBlank()) return movies
        return movies.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.originalTitle.contains(query, ignoreCase = true) ||
                    it.director.contains(query, ignoreCase = true)
        }
    }
}
