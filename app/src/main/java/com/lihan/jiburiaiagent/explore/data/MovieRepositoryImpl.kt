package com.lihan.jiburiaiagent.explore.data

import com.lihan.jiburiaiagent.core.domain.DataError
import com.lihan.jiburiaiagent.core.domain.Result
import com.lihan.jiburiaiagent.core.domain.onFailure
import com.lihan.jiburiaiagent.core.domain.onSuccess
import com.lihan.jiburiaiagent.explore.domain.Movie
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MovieRepositoryImpl(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override fun getMoviesFlow(): Flow<Result<List<Movie>, DataError.Network>> = channelFlow {
        // 1. Observe database flow and stream to UI
        val dbJob = launch {
            movieDao.getMoviesFlow().collect { entities ->
                send(Result.Success(entities.map { it.toDomain() }))
            }
        }

        // 2. Async fetch from network and sync with database
        apiService
            .getMovies()
            .onSuccess { remoteMovies ->
                val entities = remoteMovies.map { it.toEntity() }
                movieDao.upsertMoviesPreservingFavorites(entities)
            }.onFailure { error ->
                send(Result.Error(error))
            }

        // Wait until downstream cancels
        awaitClose {
            dbJob.cancel()
        }
    }

    override fun getMovieByIdFlow(id: String): Flow<Movie?> {
        return movieDao.getMovieByIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun getMovieById(id: String): Movie? {
        return movieDao.getMovieById(id)?.toDomain()
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        movieDao.updateFavorite(id, isFavorite)
    }
}
