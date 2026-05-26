package com.lihan.jiburiaiagent.explore.domain

import com.lihan.jiburiaiagent.core.domain.DataError
import com.lihan.jiburiaiagent.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesFlow(): Flow<Result<List<Movie>, DataError.Network>>
    fun getMovieByIdFlow(id: String): Flow<Movie?>
    suspend fun getMovieById(id: String): Movie?
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}
