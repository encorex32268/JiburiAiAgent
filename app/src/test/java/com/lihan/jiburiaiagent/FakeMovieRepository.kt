package com.lihan.jiburiaiagent

import com.lihan.jiburiaiagent.core.domain.DataError
import com.lihan.jiburiaiagent.core.domain.Result
import com.lihan.jiburiaiagent.explore.domain.Movie
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMovieRepository : MovieRepository {

    private val moviesFlow = MutableStateFlow<List<Movie>>(emptyList())
    var shouldReturnError = false

    fun setMovies(movies: List<Movie>) {
        moviesFlow.value = movies
    }

    override fun getMoviesFlow(): Flow<Result<List<Movie>, DataError.Network>> {
        return moviesFlow.map { list ->
            if (shouldReturnError) {
                Result.Error(DataError.Network.NO_INTERNET)
            } else {
                Result.Success(list)
            }
        }
    }

    override fun getMovieByIdFlow(id: String): Flow<Movie?> {
        return moviesFlow.map { list ->
            list.find { it.id == id }
        }
    }

    override suspend fun getMovieById(id: String): Movie? {
        return moviesFlow.value.find { it.id == id }
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        moviesFlow.update { list ->
            list.map { movie ->
                if (movie.id == id) movie.copy(isFavorite = isFavorite) else movie
            }
        }
    }
}
