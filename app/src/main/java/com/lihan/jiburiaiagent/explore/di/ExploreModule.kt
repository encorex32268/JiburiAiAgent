package com.lihan.jiburiaiagent.explore.di

import com.lihan.jiburiaiagent.explore.presentation.MovieListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val exploreModule = module {
    viewModelOf(::MovieListViewModel)
}
