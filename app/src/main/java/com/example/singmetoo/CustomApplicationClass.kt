package com.example.singmetoo

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.FirebaseApp

class CustomApplicationClass : Application() {

    companion object {
        private var instance:CustomApplicationClass? = null

        fun applicationContext() : Context? {
            return instance?.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Fresco.initialize(this)
    }
}