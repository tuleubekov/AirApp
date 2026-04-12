package com.berg.airapp

import android.app.Application
import com.berg.airapp.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AirApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AirApp)
            modules(allModules)
        }
    }
}
