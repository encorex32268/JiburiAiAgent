package com.lihan.jiburiaiagent

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.lihan.jiburiaiagent.detail.domain.GetMovieDetailUseCase
import com.lihan.jiburiaiagent.detail.domain.ToggleWatchlistUseCase
import com.lihan.jiburiaiagent.detail.presentation.MovieDetailAction
import com.lihan.jiburiaiagent.detail.presentation.MovieDetailEvent
import com.lihan.jiburiaiagent.detail.presentation.MovieDetailViewModel
import com.lihan.jiburiaiagent.explore.domain.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeMovieRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase
    private lateinit var toggleWatchlistUseCase: ToggleWatchlistUseCase
    private lateinit var viewModel: MovieDetailViewModel

    private val dummyMovies = listOf(
        Movie(
            id = "1",
            title = "My Neighbor Totoro",
            originalTitle = "となりのトトロ",
            image = "totoro.png",
            movieBanner = "totoro_banner.png",
            description = "Sisters and Totoro",
            director = "Hayao Miyazaki",
            releaseDate = "1988",
            runningTimeMinutes = 86,
            rtScore = 93,
            isFavorite = false,
            people = listOf("https://ghibliapi.vercel.app/people/satsuki")
        ),
        Movie(
            id = "2",
            title = "Spirited Away",
            originalTitle = "千と千尋の神隱し",
            image = "spirited.png",
            movieBanner = "spirited_banner.png",
            description = "Chihiro's bathhouse journey",
            director = "Hayao Miyazaki",
            releaseDate = "2001",
            runningTimeMinutes = 125,
            rtScore = 97,
            isFavorite = true,
            people = listOf("https://ghibliapi.vercel.app/people/chihiro")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMovieRepository()
        repository.setMovies(dummyMovies)
        getMovieDetailUseCase = GetMovieDetailUseCase(repository)
        toggleWatchlistUseCase = ToggleWatchlistUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init_withValidId_loadsMovieDetailAndCharactersCorrectly`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "2"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.movie)
            assertEquals("Spirited Away", state.movie?.title)
            assertTrue(state.isOnWatchlist)
            
            // Spirited Away characters should be mapped
            assertTrue(state.characters.isNotEmpty())
            assertEquals("千尋", state.characters[0].name)
            assertNull(state.error)
        }
    }

    @Test
    fun `init_withInvalidId_setsMovieNullAndEmitsNotFoundError`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "99"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.movie)
            assertNotNull(state.error)
        }
    }

    @Test
    fun `toggleWatchlist_addsToWatchlistSuccessfully`() = runTest {
        // Totoro has isFavorite = false initially
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "1"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.state.test {
            var state = awaitItem()
            assertFalse(state.isOnWatchlist)

            // Trigger action to add to watchlist
            viewModel.onAction(MovieDetailAction.OnToggleWatchlist)
            
            state = awaitItem()
            assertTrue(state.isOnWatchlist)
            
            // Verify in repository too
            val repoMovie = repository.getMovieById("1")
            assertTrue(repoMovie?.isFavorite == true)
        }
    }

    @Test
    fun `toggleWatchlist_removesFromWatchlistSuccessfully`() = runTest {
        // Spirited Away has isFavorite = true initially
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "2"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.state.test {
            var state = awaitItem()
            assertTrue(state.isOnWatchlist)

            // Trigger action to remove from watchlist
            viewModel.onAction(MovieDetailAction.OnToggleWatchlist)
            
            state = awaitItem()
            assertFalse(state.isOnWatchlist)
            
            // Verify in repository too
            val repoMovie = repository.getMovieById("2")
            assertTrue(repoMovie?.isFavorite == false)
        }
    }

    @Test
    fun `toggleWatchlist_emitsShowToastEvent`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "1"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.events.test {
            viewModel.onAction(MovieDetailAction.OnToggleWatchlist)
            val event = awaitItem()
            assertTrue(event is MovieDetailEvent.ShowToast)
        }
    }

    @Test
    fun `backClick_emitsNavigateBackEvent`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("filmId" to "1"))
        viewModel = MovieDetailViewModel(savedStateHandle, getMovieDetailUseCase, toggleWatchlistUseCase)

        viewModel.events.test {
            viewModel.onAction(MovieDetailAction.OnBackClick)
            assertEquals(MovieDetailEvent.NavigateBack, awaitItem())
        }
    }
}
