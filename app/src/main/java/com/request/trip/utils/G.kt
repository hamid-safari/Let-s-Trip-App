package com.request.trip.utils

import android.app.Application
import android.content.Context

class G : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}