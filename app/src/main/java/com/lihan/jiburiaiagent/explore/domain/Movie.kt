package com.lihan.jiburiaiagent.explore.domain

data class Movie(
    val id: String,
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
    val people: List<String> = emptyList()
)
