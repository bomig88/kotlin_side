package bomi.kotlinside

import android.app.Application
import bomi.kotlinside.base.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }

    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}