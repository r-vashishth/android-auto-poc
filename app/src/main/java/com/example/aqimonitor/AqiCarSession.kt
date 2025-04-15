package com.example.aqimonitor

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import kotlin.random.Random

class AqiCarSession : Session() {
    
    override fun onCreateScreen(intent: Intent): Screen {
        return AqiScreen(carContext)
    }
} 