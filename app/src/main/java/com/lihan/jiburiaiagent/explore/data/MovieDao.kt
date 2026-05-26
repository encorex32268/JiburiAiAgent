package com.lihan.jiburiaiagent.explore.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies_table")
    fun getMoviesFlow(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies_table WHERE id = :id")
    fun getMovieByIdFlow(id: String): Flow<MovieEntity?>

    @Query("SELECT * FROM movies_table WHERE id = :id")
    suspend fun getMovieById(id: String): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("UPDATE movies_table SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Transaction
    suspend fun upsertMoviesPreservingFavorites(newMovies: List<MovieEntity>) {
        for (movie in newMovies) {
            val existing = getMovieById(movie.id)
            if (existing != null) {
                // Preserve the existing isFavorite status
                insertMovies(listOf(movie.copy(isFavorite = existing.isFavorite)))
            } else {
                insertMovies(listOf(movie))
            }
        }
    }
}
