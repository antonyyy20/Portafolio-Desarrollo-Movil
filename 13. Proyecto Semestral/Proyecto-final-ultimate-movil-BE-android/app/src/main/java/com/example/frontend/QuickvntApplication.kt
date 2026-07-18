package com.example.frontend

import android.app.Application
import android.util.Log
import com.example.frontend.core.AppContainer

class QuickvntApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "API_BASE_URL=${BuildConfig.API_BASE_URL}")
        container = AppContainer(this)
    }

    companion object {
        private const val TAG = "Quickvnt"
    }
}
