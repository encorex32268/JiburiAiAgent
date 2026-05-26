package com.lihan.jiburiaiagent.core.di

import androidx.room.Room
import com.lihan.jiburiaiagent.core.data.JiburiDatabase
import com.lihan.jiburiaiagent.core.data.network.HttpClientFactory
import com.lihan.jiburiaiagent.explore.data.KtorMovieApiService
import com.lihan.jiburiaiagent.explore.data.MovieApiService
import com.lihan.jiburiaiagent.explore.data.MovieRepositoryImpl
import com.lihan.jiburiaiagent.explore.domain.MovieRepository
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreModule = module {
    // Ktor HttpClient configuration
    single {
        HttpClientFactory().build()
    }

    // MovieApiService configuration
    single<MovieApiService> {
        KtorMovieApiService(get())
    }

    // Room Database configuration
    single {
        Room.databaseBuilder(
            androidContext(),
            JiburiDatabase::class.java,
            "jiburi_garden.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // DAO configuration
    single {
        get<JiburiDatabase>().movieDao()
    }

    // Repository binding
    singleOf(::MovieRepositoryImpl) { bind<MovieRepository>() }
}
