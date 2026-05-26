package com.lihan.jiburiaiagent.search.di

import com.lihan.jiburiaiagent.search.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    viewModelOf(::SearchViewModel)
}
