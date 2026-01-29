package com.example.myinsta

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

const val TAG = "TESTING_HILT"
@HiltAndroidApp
class InstaApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "This is the Application Class")
    }
}