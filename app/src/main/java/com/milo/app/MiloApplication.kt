package com.milo.app

import android.app.Application
import com.milo.app.data.local.MiloDatabase

class MiloApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Pre-warm database instance
        MiloDatabase.getInstance(this)
    }
}
