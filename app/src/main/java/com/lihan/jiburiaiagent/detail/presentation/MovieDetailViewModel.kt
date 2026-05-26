package com.lihan.jiburiaiagent.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lihan.jiburiaiagent.core.presentation.UiText
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository
) : ViewModel() {

    // Retrieve filmId passed via type-safe navigation route from SavedStateHandle
    val filmId: String = savedStateHandle["filmId"] ?: ""

    private val _state = MutableStateFlow(MovieDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<MovieDetailEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadMovieDetail()
    }

    fun onAction(action: MovieDetailAction) {
        when (action) {
            is MovieDetailAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(MovieDetailEvent.NavigateBack)
                }
            }
            is MovieDetailAction.OnToggleWatchlist -> {
                toggleWatchlist()
            }
        }
    }

    private fun loadMovieDetail() {
        if (filmId.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            movieRepository.getMovieByIdFlow(filmId).collect { movie ->
                if (movie != null) {
                    val hasApiPeople = movie.people.isNotEmpty() && 
                            !(movie.people.size == 1 && movie.people[0].trimEnd('/').endsWith("people"))
                    val charactersList = if (hasApiPeople) {
                        getCharactersForMovie(movie.title)
                    } else {
                        emptyList()
                    }
                    _state.update {
                        it.copy(
                            movie = movie,
                            isOnWatchlist = movie.isFavorite,
                            characters = charactersList,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.DynamicString("Film not found")
                        )
                    }
                }
            }
        }
    }

    private fun toggleWatchlist() {
        val currentMovie = _state.value.movie ?: return
        val newFavoriteStatus = !_state.value.isOnWatchlist

        viewModelScope.launch {
            movieRepository.updateFavorite(currentMovie.id, newFavoriteStatus)
            _state.update { it.copy(isOnWatchlist = newFavoriteStatus) }
            
            val message = if (newFavoriteStatus) {
                UiText.DynamicString("Added to watchlist!")
            } else {
                UiText.DynamicString("Removed from watchlist.")
            }
            _events.send(MovieDetailEvent.ShowToast(message))
        }
    }

    private fun getCharactersForMovie(title: String): List<MovieCharacter> {
        return when {
            title.contains("Spirited Away", ignoreCase = true) || title.contains("千と千尋", ignoreCase = true) -> {
                listOf(
                    MovieCharacter(
                        name = "千尋",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLgbdmbW-fVXQLJLP9RnPrfKZFAiFoIdqmEEuoMEGZsri6mQFa3YVJD42HDebxkchACd4O8aWGivVUhGbMzUiDNWbvot3vW101Y8_6DNtJ4faAj2st6st_x_DqCRQ3CHrWgg2CNQLVvFL73vJKjf7ssjmhaa5TPooQmd-V01byDumUMqJUg49yE_G0J7rNJonSfnx-TEkNznSv5tYsOM22CRHKQ71JiiK38QBHz7zO4lfTYq7a_ToEA5nnX1rrJFOodbAW3OKFjwc"
                    ),
                    MovieCharacter(
                        name = "ハク",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBmklykel6RNyRjp5pTUtqEvXschf7HOMbocrridBGuSXm0E3ygfHoCSyhmztpGjVLA2msNCK6UrVDN5P_zHFOnYsYGppTJQ_5yTBGMktqaPh6dRbmsmdPCQG26BpHPw1s0Gxhbh3WG9vGviGLU89whTCoGykLgat14yhKnoeTpiLlkICvqUo8Z04ZeKP27hPUO0oYyGA-HievnVFJhs8qnBv6upGBLk6yRa8B_x13JcAj9FjUHWxFjO8lvpIT_7NFdUYSjdq0zNRA"
                    ),
                    MovieCharacter(
                        name = "カオナシ",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDNNeXtfedbz1PQkayeECo7oHdv2KEIrw5__VsZQf46QBzXugBIipGekrmRAdzAIXYft0HiFuMUx3ol7Opwn4jRgbJWUB8i6LNTN5nLt-IHQzJYHp6BRXkzh4xNTMK6O6Bwzw8ojp7o5qtylvRRpXgwPIeSrNmsmG9rN8tHtZaqWGHddBTEBYrJ8hbAUThiohkfqcRx1w_XILuYbi6OlV3yU5KJTkW63OMV0g2I_VCR7_0k38B8tAYOM-CV-YthApGkBbMrE-HPWc"
                    ),
                    MovieCharacter(
                        name = "湯婆婆",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQGwfHHfFnRvNQE6S9OGIph0_iyFBNmRDybLhcLIW6p1P5ogtzptsJ_eDkgU939kvhZ_LHO2f33e1na4zehti9-IefbCZXt3c0dpSn06JRdWKPVNxX0kmoJlIN3rKo2gHbLNzjPngWu90648jBbRAMhYgRXuovoRplzJUQ_UpGDkorwLWNoWQjff-DrHThbzfYrOe-gOhswXH_nFcZgg6_eVN6Quj3bNsPMBSRaWg_D4YevSeYNtfI9cjncRNhIgwJ7vbx-b8pr2Y"
                    ),
                    MovieCharacter(
                        name = "釜爺",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCibv7gZnMJU1waSda9oollzVzQhFx7cqMo8-tFaBpciMdixfCCfS3NB1daj7WUQGsoybSm5c2dQ_K2GKKdcU3mjziupTdvVO4hxjL0BOXPHuKyNLwc0hq7UTlNSkgUoz0PwCRz81_IyFfbQ0byX4DREydARlBWiDr13I47d7m7G5g7Bf-TuY2ZaTvo7XtK28wsAdt7g5hCeee3KfVjvZFtNKbS4aETQmfMwFiRUTAPEHR7BECfgWeh0OipJwu02JBbZJRcBZj4xFk"
                    )
                )
            }
            title.contains("My Neighbor Totoro", ignoreCase = true) || title.contains("トトロ", ignoreCase = true) -> {
                listOf(
                    MovieCharacter(
                        name = "サツキ",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLgbdmbW-fVXQLJLP9RnPrfKZFAiFoIdqmEEuoMEGZsri6mQFa3YVJD42HDebxkchACd4O8aWGivVUhGbMzUiDNWbvot3vW101Y8_6DNtJ4faAj2st6st_x_DqCRQ3CHrWgg2CNQLVvFL73vJKjf7ssjmhaa5TPooQmd-V01byDumUMqJUg49yE_G0J7rNJonSfnx-TEkNznSv5tYsOM22CRHKQ71JiiK38QBHz7zO4lfTYq7a_ToEA5nnX1rrJFOodbAW3OKFjwc"
                    ),
                    MovieCharacter(
                        name = "メイ",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBmklykel6RNyRjp5pTUtqEvXschf7HOMbocrridBGuSXm0E3ygfHoCSyhmztpGjVLA2msNCK6UrVDN5P_zHFOnYsYGppTJQ_5yTBGMktqaPh6dRbmsmdPCQG26BpHPw1s0Gxhbh3WG9vGviGLU89whTCoGykLgat14yhKnoeTpiLlkICvqUo8Z04ZeKP27hPUO0oYyGA-HievnVFJhs8qnBv6upGBLk6yRa8B_x13JcAj9FjUHWxFjO8lvpIT_7NFdUYSjdq0zNRA"
                    ),
                    MovieCharacter(
                        name = "トトロ",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDNNeXtfedbz1PQkayeECo7oHdv2KEIrw5__VsZQf46QBzXugBIipGekrmRAdzAIXYft0HiFuMUx3ol7Opwn4jRgbJWUB8i6LNTN5nLt-IHQzJYHp6BRXkzh4xNTMK6O6Bwzw8ojp7o5qtylvRRpXgwPIeSrNmsmG9rN8tHtZaqWGHddBTEBYrJ8hbAUThiohkfqcRx1w_XILuYbi6OlV3yU5KJTkW63OMV0g2I_VCR7_0k38B8tAYOM-CV-YthApGkBbMrE-HPWc"
                    )
                )
            }
            else -> {
                // Fallbacks for other Ghibli movies
                listOf(
                    MovieCharacter(name = "Character A", imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCLgbdmbW-fVXQLJLP9RnPrfKZFAiFoIdqmEEuoMEGZsri6mQFa3YVJD42HDebxkchACd4O8aWGivVUhGbMzUiDNWbvot3vW101Y8_6DNtJ4faAj2st6st_x_DqCRQ3CHrWgg2CNQLVvFL73vJKjf7ssjmhaa5TPooQmd-V01byDumUMqJUg49yE_G0J7rNJonSfnx-TEkNznSv5tYsOM22CRHKQ71JiiK38QBHz7zO4lfTYq7a_ToEA5nnX1rrJFOodbAW3OKFjwc"),
                    MovieCharacter(name = "Character B", imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBmklykel6RNyRjp5pTUtqEvXschf7HOMbocrridBGuSXm0E3ygfHoCSyhmztpGjVLA2msNCK6UrVDN5P_zHFOnYsYGppTJQ_5yTBGMktqaPh6dRbmsmdPCQG26BpHPw1s0Gxhbh3WG9vGviGLU89whTCoGykLgat14yhKnoeTpiLlkICvqUo8Z04ZeKP27hPUO0oYyGA-HievnVFJhs8qnBv6upGBLk6yRa8B_x13JcAj9FjUHWxFjO8lvpIT_7NFdUYSjdq0zNRA")
                )
            }
        }
    }
}
