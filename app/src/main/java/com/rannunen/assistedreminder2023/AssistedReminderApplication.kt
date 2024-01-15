package com.rannunen.assistedreminder2023

import android.app.Application

class AssistedReminderApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}