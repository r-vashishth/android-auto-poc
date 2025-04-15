package com.example.aqimonitor.automotive

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

class AqiCarSession : Session() {
    
    override fun onCreateScreen(intent: Intent): Screen {
        return AqiScreen(carContext)
    }
} 