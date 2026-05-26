package com.lihan.jiburiaiagent.di

import androidx.lifecycle.SavedStateHandle
import com.lihan.jiburiaiagent.FakeMovieRepository
import com.lihan.jiburiaiagent.detail.di.detailModule
import com.lihan.jiburiaiagent.detail.presentation.MovieDetailViewModel
import com.lihan.jiburiaiagent.explore.di.exploreModule
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import com.lihan.jiburiaiagent.explore.presentation.MovieListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

@OptIn(ExperimentalCoroutinesApi::class)
class DependencyInjectionTest : KoinTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testCoreModule = module {
        single<MovieRepository> { FakeMovieRepository() }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun verifyKoinModules() {
        startKoin {
            modules(
                testCoreModule,
                exploreModule,
                detailModule
            )
        }

        // Verify that MovieListViewModel can be resolved with all UseCases
        val movieListViewModel = get<MovieListViewModel>()
        assertNotNull(movieListViewModel)

        // Verify that MovieDetailViewModel can be resolved with all UseCases and SavedStateHandle
        val movieDetailViewModel = get<MovieDetailViewModel> {
            parametersOf(SavedStateHandle(mapOf("filmId" to "1")))
        }
        assertNotNull(movieDetailViewModel)
    }
}
