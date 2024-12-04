package com.example.puttiponggrinpoon

import android.app.Application

class CoffeeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize CartManager when application starts
        CartManager.initialize(this)
    }
}