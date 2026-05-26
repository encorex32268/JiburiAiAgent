package com.lihan.jiburiaiagent

import android.app.Application
import com.lihan.jiburiaiagent.core.di.coreModule
import com.lihan.jiburiaiagent.detail.di.detailModule
import com.lihan.jiburiaiagent.explore.di.exploreModule
import com.lihan.jiburiaiagent.search.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                coreModule,
                exploreModule,
                detailModule,
                searchModule
            )
        }
    }
}
