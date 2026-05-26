package com.lihan.jiburiaiagent.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.jiburiaiagent.core.domain.Result
import com.lihan.jiburiaiagent.core.domain.onSuccess
import com.lihan.jiburiaiagent.core.domain.onFailure
import com.lihan.jiburiaiagent.core.presentation.toUiText
import com.lihan.jiburiaiagent.explore.domain.Movie
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()

    private val _events = Channel<MovieListEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadMovies()
    }

    fun onAction(action: MovieListAction) {
        when (action) {
            is MovieListAction.OnMovieClick -> {
                viewModelScope.launch {
                    _events.send(MovieListEvent.NavigateToDetail(action.movieId))
                }
            }
            is MovieListAction.OnCategorySelect -> {
                updateStateAndFilter { it.copy(selectedCategory = action.category) }
            }
            is MovieListAction.OnTabSelect -> {
                _state.update { it.copy(activeTab = action.tab) }
            }
            is MovieListAction.OnRefresh -> {
                loadMovies()
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            movieRepository.getMoviesFlow().collect { result ->
                result.onSuccess { moviesList ->
                    // Find Featured Movie: My Neighbor Totoro or highest rated
                    val totoro = moviesList.find { it.title.contains("Totoro", ignoreCase = true) }
                    val featured = totoro ?: moviesList.maxByOrNull { it.rtScore }

                    // Find Coming Soon Movie: The Boy and the Heron or newest
                    val boyAndHeron = moviesList.find { 
                        it.title.contains("Heron", ignoreCase = true) || 
                        it.title.contains("Kimi-tachi", ignoreCase = true) 
                    }
                    val comingSoon = boyAndHeron ?: moviesList.maxByOrNull { it.releaseDate }

                    // Exclude featured and coming soon from standard grid exploration to avoid duplication
                    val explorationMovies = moviesList.filter { 
                        it.id != featured?.id && it.id != comingSoon?.id 
                    }

                    updateStateAndFilter { 
                        it.copy(
                            movies = explorationMovies,
                            allMovies = moviesList,
                            featuredMovie = featured,
                            comingSoonMovie = comingSoon,
                            isLoading = false
                        ) 
                    }
                }.onFailure { error ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = error.toUiText()
                        ) 
                    }
                    _events.send(MovieListEvent.ShowSnackbar(error.toUiText()))
                }
            }
        }
    }

    private fun updateStateAndFilter(updateBlock: (MovieListState) -> MovieListState) {
        _state.update { currentState ->
            val updatedState = updateBlock(currentState)
            val filtered = updatedState.movies.filter { movie ->
                movie.belongsToCategory(updatedState.selectedCategory)
            }
            updatedState.copy(filteredMovies = filtered)
        }
    }

    private fun Movie.belongsToCategory(category: String): Boolean {
        if (category == "All") return true
        val lowercaseTitle = title.lowercase()
        val lowercaseDesc = description.lowercase()
        return when (category) {
            "Fantasy" -> {
                lowercaseTitle.contains("spirited away") || 
                lowercaseTitle.contains("totoro") || 
                lowercaseTitle.contains("howl") || 
                lowercaseTitle.contains("castle in the sky") || 
                lowercaseTitle.contains("kiki") || 
                lowercaseTitle.contains("ponyo") || 
                lowercaseTitle.contains("arrietty") || 
                lowercaseTitle.contains("kaguya") || 
                lowercaseTitle.contains("cat returns") ||
                lowercaseDesc.contains("magic") || 
                lowercaseDesc.contains("fantasy") || 
                lowercaseDesc.contains("spirit")
            }
            "Adventure" -> {
                lowercaseTitle.contains("mononoke") || 
                lowercaseTitle.contains("nausicaa") || 
                lowercaseTitle.contains("sky") || 
                lowercaseTitle.contains("porco") || 
                lowercaseTitle.contains("earthsea") || 
                lowercaseDesc.contains("adventure") || 
                lowercaseDesc.contains("journey") || 
                lowercaseDesc.contains("battle")
            }
            "Drama" -> {
                lowercaseTitle.contains("grave") || 
                lowercaseTitle.contains("yesterday") || 
                lowercaseTitle.contains("wind rises") || 
                lowercaseTitle.contains("whisper of the heart") || 
                lowercaseTitle.contains("poppy hill") || 
                lowercaseTitle.contains("yamadas") || 
                lowercaseTitle.contains("ocean waves") ||
                lowercaseDesc.contains("drama") || 
                lowercaseDesc.contains("war") || 
                lowercaseDesc.contains("life") ||
                lowercaseDesc.contains("love")
            }
            "Family" -> {
                lowercaseTitle.contains("totoro") || 
                lowercaseTitle.contains("kiki") || 
                lowercaseTitle.contains("ponyo") || 
                lowercaseTitle.contains("yamadas") || 
                lowercaseTitle.contains("arrietty") || 
                lowercaseTitle.contains("cat returns") ||
                lowercaseDesc.contains("family") || 
                lowercaseDesc.contains("children") || 
                lowercaseDesc.contains("sister")
            }
            else -> false
        }
    }
}
