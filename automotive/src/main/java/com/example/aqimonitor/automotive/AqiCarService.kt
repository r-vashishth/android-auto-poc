package com.example.aqimonitor.automotive

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AqiCarService : Service() {
    private val TAG = "AqiCarService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AQI Car Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "AQI Car Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AQI Car Service destroyed")
    }
} 