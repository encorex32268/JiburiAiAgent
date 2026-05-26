package com.lihan.jiburiaiagent.explore.data

import com.lihan.jiburiaiagent.core.domain.DataError
import com.lihan.jiburiaiagent.core.domain.Result
import com.lihan.jiburiaiagent.core.data.network.get
import io.ktor.client.HttpClient

interface MovieApiService {
    suspend fun getMovies(): Result<List<MovieDto>, DataError.Network>
}

class KtorMovieApiService(
    private val httpClient: HttpClient
) : MovieApiService {
    override suspend fun getMovies(): Result<List<MovieDto>, DataError.Network> {
        return httpClient.get("films")
    }
}
