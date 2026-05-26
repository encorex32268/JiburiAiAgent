package com.lihan.jiburiaiagent.explore.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lihan.jiburiaiagent.explore.domain.Movie

@Entity(tableName = "movies_table")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val originalTitle: String,
    val image: String,
    val movieBanner: String,
    val description: String,
    val director: String,
    val releaseDate: String,
    val runningTimeMinutes: Int,
    val rtScore: Int,
    val isFavorite: Boolean = false,
    val peopleCsv: String = ""
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    originalTitle = originalTitle,
    image = image,
    movieBanner = movieBanner,
    description = description,
    director = director,
    releaseDate = releaseDate,
    runningTimeMinutes = runningTimeMinutes,
    rtScore = rtScore,
    isFavorite = isFavorite,
    people = if (peopleCsv.isEmpty()) emptyList() else peopleCsv.split(",")
)

fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    originalTitle = originalTitle,
    image = image,
    movieBanner = movieBanner,
    description = description,
    director = director,
    releaseDate = releaseDate,
    runningTimeMinutes = runningTimeMinutes,
    rtScore = rtScore,
    isFavorite = isFavorite,
    peopleCsv = people.joinToString(",")
)

fun MovieDto.toEntity(isFavorite: Boolean = false): MovieEntity = MovieEntity(
    id = id,
    title = title,
    originalTitle = original_title,
    image = image,
    movieBanner = movie_banner,
    description = description,
    director = director,
    releaseDate = release_date,
    runningTimeMinutes = running_time.toIntOrNull() ?: 0,
    rtScore = rt_score.toIntOrNull() ?: 0,
    isFavorite = isFavorite,
    peopleCsv = people.joinToString(",")
)
