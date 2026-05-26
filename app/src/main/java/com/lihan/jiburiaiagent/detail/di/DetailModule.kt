package com.lihan.jiburiaiagent.detail.di

import com.lihan.jiburiaiagent.detail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailModule = module {
    viewModelOf(::MovieDetailViewModel)
}
