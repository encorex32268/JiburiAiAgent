package com.lihan.jiburiaiagent

import app.cash.turbine.test
import com.lihan.jiburiaiagent.explore.domain.GetMoviesUseCase
import com.lihan.jiburiaiagent.explore.domain.Movie
import com.lihan.jiburiaiagent.explore.presentation.MovieListAction
import com.lihan.jiburiaiagent.explore.presentation.MovieListEvent
import com.lihan.jiburiaiagent.explore.presentation.MovieListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeMovieRepository
    private lateinit var getMoviesUseCase: GetMoviesUseCase
    private lateinit var viewModel: MovieListViewModel

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
            rtScore = 93
        ),
        Movie(
            id = "2",
            title = "Spirited Away",
            originalTitle = "千と千尋の神隠し",
            image = "spirited.png",
            movieBanner = "spirited_banner.png",
            description = "Chihiro's bathhouse journey",
            director = "Hayao Miyazaki",
            releaseDate = "2001",
            runningTimeMinutes = 125,
            rtScore = 97
        ),
        Movie(
            id = "3",
            title = "The Boy and the Heron",
            originalTitle = "君たちはどう生きるか",
            image = "heron.png",
            movieBanner = "heron_banner.png",
            description = "眞人's fantastical journey",
            director = "Hayao Miyazaki",
            releaseDate = "2023",
            runningTimeMinutes = 124,
            rtScore = 97
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMovieRepository()
        getMoviesUseCase = GetMoviesUseCase(repository)
        repository.setMovies(dummyMovies)
        viewModel = MovieListViewModel(getMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init_loadsMoviesCorrectlyAndAssignsSpecialBanners`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            // My Neighbor Totoro is Featured
            assertEquals("My Neighbor Totoro", state.featuredMovie?.title)
            
            // The Boy and the Heron is Coming Soon
            assertEquals("The Boy and the Heron", state.comingSoonMovie?.title)
            
            // Standard exploration contains only "Spirited Away" (others excluded to prevent duplicates)
            assertEquals(1, state.movies.size)
            assertEquals("Spirited Away", state.movies[0].title)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `categorySelect_filtersMoviesCorrectly`() = runTest {
        viewModel.state.test {
            // Initial load
            var state = awaitItem()
            assertEquals("All", state.selectedCategory)
            
            // Select "Fantasy" category
            viewModel.onAction(MovieListAction.OnCategorySelect("Fantasy"))
            state = awaitItem()
            assertEquals("Fantasy", state.selectedCategory)
            // Spirited Away belongs to Fantasy, should be visible
            assertEquals(1, state.filteredMovies.size)
            assertEquals("Spirited Away", state.filteredMovies[0].title)
            
            // Select "Drama" category
            viewModel.onAction(MovieListAction.OnCategorySelect("Drama"))
            state = awaitItem()
            assertEquals("Drama", state.selectedCategory)
            // Spirited Away is not drama, filtered list should be empty
            assertEquals(0, state.filteredMovies.size)
        }
    }

    @Test
    fun `movieClick_emitsNavigateToDetailEvent`() = runTest {
        viewModel.events.test {
            viewModel.onAction(MovieListAction.OnMovieClick("2"))
            assertEquals(MovieListEvent.NavigateToDetail("2"), awaitItem())
        }
    }

    @Test
    fun `networkError_emitsSnackbarEvent`() = runTest {
        // Set repository to return error
        repository.shouldReturnError = true
        
        // Re-trigger load via refresh action
        viewModel.onAction(MovieListAction.OnRefresh)
        
        viewModel.events.test {
            val event = awaitItem()
            assertTrue(event is MovieListEvent.ShowSnackbar)
        }
    }
}
